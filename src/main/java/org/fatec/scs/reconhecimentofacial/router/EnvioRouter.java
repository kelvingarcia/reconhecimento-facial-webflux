package org.fatec.scs.reconhecimentofacial.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.fatec.scs.reconhecimentofacial.dto.auxiliar.PredicaoConfianca;
import org.fatec.scs.reconhecimentofacial.dto.request.ReconheceRequest;
import org.fatec.scs.reconhecimentofacial.dto.request.TreinaRequest;
import org.fatec.scs.reconhecimentofacial.dto.response.PessoaDTO;
import org.fatec.scs.reconhecimentofacial.dto.response.ReconheceResponse;
import org.fatec.scs.reconhecimentofacial.model.Pessoa;
import org.fatec.scs.reconhecimentofacial.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EnvioRouter {

    @Autowired
    private EnvioService envioService;

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route()
                .POST("/envioTreinamento", req -> ok().body(
                        req.bodyToMono(TreinaRequest.class).flatMap(t -> envioService.treinaMobile(t)),
                        Pessoa.class
                ))
                .POST("/envioTreinamentoWeb", req -> ok().body(
                        req.bodyToMono(TreinaRequest.class).flatMap(t -> envioService.treina(t)),
                        Pessoa.class
                ))
                .POST("/reconheceFoto", req -> ok().body(
                        req.bodyToMono(ReconheceRequest.class)
                            .flatMap(r -> envioService.mandaFotoParaReconhecimento(r)),
                        ReconheceResponse.class
                ))
                .GET("/esperaReconhecimento/{id}", req -> ok()
                        .contentType(MediaType.APPLICATION_STREAM_JSON)
                        .body(envioService.reconhecePessoa(req.pathVariable("id")),
                        PessoaDTO.class
                ))
                .GET("/testaFlux", req -> ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(envioService.mensagemStream(),
                        PessoaDTO.class
                ))
                .GET("/predicoes/{id}", req -> ok().contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(envioService.testaPredicao(req.pathVariable("id")), PredicaoConfianca.class)
                )
                .GET("/pessoas", req -> ok().body(envioService.getPessoasBanco(), PessoaDTO.class))
                .GET("/email/{email}", req -> ok().body(envioService.buscaPorEmail(req.pathVariable("email")), Pessoa.class))
                .build();
    }
    
}
