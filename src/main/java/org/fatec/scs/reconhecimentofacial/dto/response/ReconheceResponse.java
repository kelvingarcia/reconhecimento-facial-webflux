package org.fatec.scs.reconhecimentofacial.dto.response;

public class ReconheceResponse {
    private String nomePessoa;
    private String mensagem;

    public ReconheceResponse() {
    }

    public ReconheceResponse(String nomePessoa, String mensagem) {
        this.nomePessoa = nomePessoa;
        this.mensagem = mensagem;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
