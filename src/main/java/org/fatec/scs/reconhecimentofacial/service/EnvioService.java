package org.fatec.scs.reconhecimentofacial.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.bytedeco.javacv.FrameFilter;
import org.fatec.scs.reconhecimentofacial.component.IdentificadorFaces;
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

    @Autowired
    private IdentificadorFaces identificadorFaces;

    private Flux<Reconhecimento> reconhecimentoFlux;

    public EnvioService() {
        this.reconhecimentoFlux = Flux.empty();
    }

    public Mono<Reconhecimento> reconhecePessoa(byte[] video) {
        try {
            Files.write(Paths.get("src/main/resources/video/novoReconhecimento.mp4"), video);
        }catch (Exception e){
            e.printStackTrace();
        }
        return this.reconhecedorFacial.reconhecePessoa(video);
    }

    public Mono<PessoaDTO> treina(TreinaRequest treinaRequest) {
    	logger.info("Treinamento recebido");
        try {
            Files.write(Paths.get("src/main/resources/video/novoReconhecimento.mp4"), treinaRequest.getVideo());
        }catch (Exception e){
            e.printStackTrace();
        }
        return this.pessoaRepository.save(new Pessoa(
                treinaRequest.getNome(),
                treinaRequest.getEmail(),
                this.reconhecedorFacial.getProximaClasse().getAndIncrement(),
                treinaRequest.getVideo(),
                false
        )).map(pessoa -> new PessoaDTO(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(), pessoa.getClasse()));
    }

    public Mono<PessoaDTO> treinaMobile(TreinaRequest treinaRequest) {
        logger.info("Treinamento recebido");
        return this.pessoaRepository.save(new Pessoa(
                treinaRequest.getNome(),
                treinaRequest.getEmail(),
                this.reconhecedorFacial.getProximaClasse().getAndIncrement(),
                treinaRequest.getVideo(),
                true
        )).map(pessoa -> new PessoaDTO(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(), pessoa.getClasse()));
    }
    
    public Mono<Pessoa> buscaPorEmail(String email) {
    	return this.pessoaRepository.findByEmail(email);
    }

    public Mono<PessoaDTO> buscaPorId(String id){
        return this.pessoaRepository.findById(id)
                .map(pessoa -> new PessoaDTO(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(), pessoa.getClasse()));
    }

    public Flux<PessoaDTO> getPessoasBanco(){
    	logger.info("Recebeu requisição");
        return this.pessoaRepository.findAll().map(pessoa -> new PessoaDTO(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(), pessoa.getClasse()));
    }

    public Mono<String> deletaPessoa(String nome){
        this.pessoaRepository.findByNome(nome)
            .subscribe(pessoa -> this.pessoaRepository.delete(pessoa));
        return Mono.just("Deletou");
    }

}
