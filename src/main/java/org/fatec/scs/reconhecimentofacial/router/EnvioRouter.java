package org.fatec.scs.reconhecimentofacial.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.fatec.scs.reconhecimentofacial.dto.auxiliar.PredicaoConfianca;
import org.fatec.scs.reconhecimentofacial.dto.auxiliar.Reconhecimento;
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
                        PessoaDTO.class
                ))
                .POST("/envioTreinamentoWeb", req -> ok().body(
                        req.bodyToMono(TreinaRequest.class).flatMap(t -> envioService.treina(t)),
                        PessoaDTO.class
                ))
                .POST("/reconhecePessoa", req -> ok().body(
                        req.bodyToMono(ReconheceRequest.class)
                        .flatMap(reconheceRequest -> envioService.reconhecePessoa(reconheceRequest.getVideo())),
                        Reconhecimento.class
                ))
                .GET("/pessoas", req -> ok().body(envioService.getPessoasBanco(), PessoaDTO.class))
                .GET("/email/{email}", req -> ok().body(envioService.buscaPorEmail(req.pathVariable("email")), Pessoa.class))
                .GET("/pessoa/{id}", req -> ok().body(envioService.buscaPorId(req.pathVariable("id")), PessoaDTO.class))
                .GET("/deleta/{nome}", req -> ok().body(envioService.deletaPessoa(req.pathVariable("nome")), String.class))
                .build();
    }
    
}
