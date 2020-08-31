package org.fatec.scs.reconhecimentofacial.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.fatec.scs.reconhecimentofacial.component.ReconhecedorFacial;
import org.fatec.scs.reconhecimentofacial.dto.auxiliar.PredicaoConfianca;
import org.fatec.scs.reconhecimentofacial.dto.auxiliar.Reconhecimento;
import org.fatec.scs.reconhecimentofacial.dto.enums.StatusReconhecimento;
import org.fatec.scs.reconhecimentofacial.dto.request.ReconheceRequest;
import org.fatec.scs.reconhecimentofacial.dto.request.TreinaRequest;
import org.fatec.scs.reconhecimentofacial.dto.response.PessoaDTO;
import org.fatec.scs.reconhecimentofacial.dto.response.ReconheceResponse;
import org.fatec.scs.reconhecimentofacial.model.Pessoa;
import org.fatec.scs.reconhecimentofacial.repository.PessoaRepository;
import org.fatec.scs.reconhecimentofacial.util.MonoSinkPessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EnvioService {

    private static final Logger logger = LoggerFactory.getLogger(EnvioService.class);

    @Autowired
    private ReconhecedorFacial reconhecedorFacial;

    @Autowired
    private PessoaRepository pessoaRepository;

    private Flux<Reconhecimento> reconhecimentoFlux;

    public EnvioService() {
        this.reconhecimentoFlux = Flux.empty();
    }

    public Flux<PredicaoConfianca> testaPredicao(String id) {
        return this.reconhecimentoFlux
                .filter(rec ->
                        rec.getPessoa().getId().equals(id)
                )
                .flatMap(rec -> rec.getPredicoes());
    }

    public Mono<PessoaDTO> reconhecePessoa(String id) {
//        MonoSinkReconhecimento monoSinkReconhecimento = new MonoSinkReconhecimento();
    	MonoSinkPessoa monoSinkPessoa = new MonoSinkPessoa();
        Reconhecimento reconhecimento = new Reconhecimento(LocalDateTime.now(), monoSinkPessoa);
        this.pessoaRepository.findById(id).subscribe(pessoa -> {
            reconhecimento.setPessoa(pessoa);
            this.reconhecimentoFlux = this.reconhecimentoFlux
                    .filter(rec -> !rec.getPessoa().getId().equals(id))
                    .concatWithValues(reconhecimento);
        });
        return Mono.create(monoSinkPessoa)
                .timeout(Duration.ofMinutes(30), Mono.empty())
                .doOnCancel(() -> logger.debug("teste"));
    }

    public Mono<ReconheceResponse> mandaFotoParaReconhecimento(ReconheceRequest reconheceRequest) {
        this.reconhecimentoFlux
        .filter(rec ->
                rec.getPessoa().getId().equals(reconheceRequest.getIdPessoa())
        )
        .subscribe(reconhecimento -> {
            PredicaoConfianca predicaoConfianca = this.reconhecedorFacial.reconhecePessoa(reconheceRequest.getFoto());
            int predicao = predicaoConfianca.predicao();
            reconhecimento.getFluxSinkPredicaoConfianca().publishPredicaoConfianca(predicaoConfianca);
            reconhecimento.setNumeroFotos(reconhecimento.getNumeroFotos() + 1);
            if (predicao == reconhecimento.getPessoa().getClasse()) {
                reconhecimento.setFotosReconhecidas(reconhecimento.getFotosReconhecidas() + 1);
            }

            if (reconhecimento.getNumeroFotos() >= 5 && reconhecimento.getFotosReconhecidas() / reconhecimento.getNumeroFotos() >= 0.8) {
                reconhecimento.setStatusReconhecimento(StatusReconhecimento.RECONHECIDO_CORRETAMENTE);
                reconhecimento.getMonoSinkPessoa().publishPessoa(new PessoaDTO(reconhecimento.getPessoa().getId(), reconhecimento.getPessoa().getNome(), reconhecimento.getPessoa().getClasse()));
            } else if (reconhecimento.getNumeroFotos() > 7 && !(reconhecimento.getFotosReconhecidas() / reconhecimento.getNumeroFotos() >= 0.8)) {
                reconhecimento.setStatusReconhecimento(StatusReconhecimento.RECONHECIDO_INCORRETAMENTE);
                reconhecimento.getMonoSinkPessoa().publishPessoa(new PessoaDTO(reconhecimento.getPessoa().getId(), reconhecimento.getPessoa().getNome(), reconhecimento.getPessoa().getClasse()));
            }
        });
        return Mono.just(new ReconheceResponse(reconheceRequest.getIdPessoa(),
                "Aguarde a resposta no endpoint /esperaReconhecimento passando o id da pessoa"));
    }

    public Mono<Pessoa> treina(TreinaRequest treinaRequest) {
    	logger.info("Treinamento recebido");
        return this.pessoaRepository.save(new Pessoa(
                treinaRequest.getNome(),
                treinaRequest.getEmail(),
                this.reconhecedorFacial.getProximaClasse().getAndIncrement(),
                treinaRequest.getVideo()
        ));
    }
    
    public Mono<Pessoa> buscaPorEmail(String email) {
    	return this.pessoaRepository.findByEmail(email);
    }

    public Flux<PessoaDTO> getPessoasBanco(){
    	logger.info("Recebeu requisição");
        return this.pessoaRepository.findAll().map(pessoa -> new PessoaDTO(pessoa.getId(), pessoa.getNome(), pessoa.getClasse()));
    }
    
    public Flux<PessoaDTO> mensagemStream(){
		return Flux.fromStream(
				Stream.generate(() -> new PessoaDTO("teste", "Olá", 1))
		).delayElements(Duration.ofSeconds(3));
	}
}
