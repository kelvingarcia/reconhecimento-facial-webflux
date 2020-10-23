package org.fatec.scs.reconhecimentofacial.dto.auxiliar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.fatec.scs.reconhecimentofacial.dto.enums.StatusReconhecimento;
import org.fatec.scs.reconhecimentofacial.dto.response.PessoaDTO;
import org.fatec.scs.reconhecimentofacial.model.Pessoa;
import org.fatec.scs.reconhecimentofacial.util.FluxSinkPredicaoConfianca;
import org.fatec.scs.reconhecimentofacial.util.MonoSinkPessoa;

import reactor.core.publisher.Flux;

public class Reconhecimento {
    private PessoaDTO pessoa;
    private PredicaoConfianca predicaoConfianca;
    private StatusReconhecimento statusReconhecimento;

    public Reconhecimento() {
    }

    public PessoaDTO getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaDTO pessoa) {
        this.pessoa = pessoa;
    }

    public PredicaoConfianca getPredicaoConfianca() {
        return predicaoConfianca;
    }

    public void setPredicaoConfianca(PredicaoConfianca predicaoConfianca) {
        this.predicaoConfianca = predicaoConfianca;
    }

    public StatusReconhecimento getStatusReconhecimento() {
        return statusReconhecimento;
    }

    public void setStatusReconhecimento(StatusReconhecimento statusReconhecimento) {
        this.statusReconhecimento = statusReconhecimento;
    }
}
