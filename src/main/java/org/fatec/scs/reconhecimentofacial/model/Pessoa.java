package org.fatec.scs.reconhecimentofacial.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Pessoa {
    @Id
    private String id;
    private String nome;
    private String email;
    private int classe;
    private boolean mobile;
    private byte[] video;

    public Pessoa() {
    }

    public Pessoa(String nome, String email, int classe, byte[] video, boolean mobile) {
        this.nome = nome;
        this.email = email;
        this.classe = classe;
        this.video = video;
        this.mobile = mobile;
    }

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getClasse() {
        return classe;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public byte[] getVideo() {
        return video;
    }

    public void setVideo(byte[] video) {
        this.video = video;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }
}
