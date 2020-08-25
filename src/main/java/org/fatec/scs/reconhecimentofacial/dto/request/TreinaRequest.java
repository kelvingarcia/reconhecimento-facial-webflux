package org.fatec.scs.reconhecimentofacial.dto.request;

public class TreinaRequest {
    private String nome;
    private String email;
    private byte[] video;

    public TreinaRequest() {
    }

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getVideo() {
		return video;
	}

	public void setVideo(byte[] video) {
		this.video = video;
	}

}
