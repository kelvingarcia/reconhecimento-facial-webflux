package org.fatec.scs.reconhecimentofacial.dto.auxiliar;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PredicaoConfianca(int predicao, double confianca) {
}
