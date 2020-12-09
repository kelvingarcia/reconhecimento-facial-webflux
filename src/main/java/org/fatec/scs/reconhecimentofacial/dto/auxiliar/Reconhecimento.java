package org.fatec.scs.reconhecimentofacial.dto.auxiliar;

import org.fatec.scs.reconhecimentofacial.dto.enums.StatusReconhecimento;
import org.fatec.scs.reconhecimentofacial.dto.response.PessoaDTO;

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
