package org.fatec.scs.reconhecimentofacial.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PessoaDTO(String id, String nome, String email, int classe){
}