package org.fatec.scs.reconhecimentofacial.util;

import java.util.function.Consumer;

import org.fatec.scs.reconhecimentofacial.dto.response.PessoaDTO;

import reactor.core.publisher.MonoSink;

public class MonoSinkPessoa implements Consumer<MonoSink<PessoaDTO>>{
	
	private MonoSink<PessoaDTO> monoSink;

	@Override
	public void accept(MonoSink<PessoaDTO> t) {
		this.monoSink = t;
	}
	
	public void publishPessoa(PessoaDTO pessoa) {
		this.monoSink.success(pessoa);
	}

}
