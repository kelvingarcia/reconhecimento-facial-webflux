package org.fatec.scs.reconhecimentofacial.repository;

import org.fatec.scs.reconhecimentofacial.model.Pessoa;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface PessoaRepository extends ReactiveCrudRepository<Pessoa, String> {
	Mono<Pessoa> findByEmail(String email);
}
