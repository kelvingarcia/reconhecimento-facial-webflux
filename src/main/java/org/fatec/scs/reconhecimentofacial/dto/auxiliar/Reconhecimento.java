package org.fatec.scs.reconhecimentofacial.dto.auxiliar;

import org.fatec.scs.reconhecimentofacial.dto.enums.StatusReconhecimento;
import org.fatec.scs.reconhecimentofacial.model.Pessoa;
import org.fatec.scs.reconhecimentofacial.util.FluxSinkPredicaoConfianca;
import org.fatec.scs.reconhecimentofacial.util.MonoSinkReconhecimento;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public class Reconhecimento {
    private Pessoa pessoa;
    private int numeroFotos;
    private int fotosReconhecidas;
    private double confianca;
    private LocalDateTime horaInicial;
    private Flux<PredicaoConfianca> predicoes;
    private FluxSinkPredicaoConfianca fluxSinkPredicaoConfianca;
    private MonoSinkReconhecimento monoSinkReconhecimento;
    private StatusReconhecimento statusReconhecimento;

    public Reconhecimento() {
    }

    public Reconhecimento(LocalDateTime horaInicial, MonoSinkReconhecimento monoSinkReconhecimento) {
        this.horaInicial = horaInicial;
        this.monoSinkReconhecimento = monoSinkReconhecimento;
        this.fluxSinkPredicaoConfianca = new FluxSinkPredicaoConfianca();
        this.predicoes = Flux.create(this.fluxSinkPredicaoConfianca);
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public int getNumeroFotos() {
        return numeroFotos;
    }

    public void setNumeroFotos(int numeroFotos) {
        this.numeroFotos = numeroFotos;
    }

    public double getConfianca() {
        return confianca;
    }

    public void setConfianca(double confianca) {
        this.confianca = confianca;
    }

    public StatusReconhecimento getStatusReconhecimento() {
        return statusReconhecimento;
    }

    public void setStatusReconhecimento(StatusReconhecimento statusReconhecimento) {
        this.statusReconhecimento = statusReconhecimento;
    }

    public LocalDateTime getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(LocalDateTime horaInicial) {
        this.horaInicial = horaInicial;
    }

    public MonoSinkReconhecimento getMonoSinkReconhecimento() {
        return monoSinkReconhecimento;
    }

    public void setMonoSinkReconhecimento(MonoSinkReconhecimento monoSinkReconhecimento) {
        this.monoSinkReconhecimento = monoSinkReconhecimento;
    }

    public int getFotosReconhecidas() {
        return fotosReconhecidas;
    }

    public void setFotosReconhecidas(int fotosReconhecidas) {
        this.fotosReconhecidas = fotosReconhecidas;
    }

    public Flux<PredicaoConfianca> getPredicoes() {
        return predicoes;
    }

    public void setPredicoes(Flux<PredicaoConfianca> predicoes) {
        this.predicoes = predicoes;
    }

    public FluxSinkPredicaoConfianca getFluxSinkPredicaoConfianca() {
        return fluxSinkPredicaoConfianca;
    }

    public void setFluxSinkPredicaoConfianca(FluxSinkPredicaoConfianca fluxSinkPredicaoConfianca) {
        this.fluxSinkPredicaoConfianca = fluxSinkPredicaoConfianca;
    }
}
