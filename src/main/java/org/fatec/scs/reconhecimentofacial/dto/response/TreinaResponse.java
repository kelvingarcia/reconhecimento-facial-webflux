package org.fatec.scs.reconhecimentofacial.dto.response;

public class TreinaResponse {
    private String pessoa;
    private String status;

    public TreinaResponse() {
    }

    public TreinaResponse(String pessoa, String status) {
        this.pessoa = pessoa;
        this.status = status;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
