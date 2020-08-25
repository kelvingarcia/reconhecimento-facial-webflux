package org.fatec.scs.reconhecimentofacial.util;

import org.fatec.scs.reconhecimentofacial.dto.auxiliar.Reconhecimento;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public class FluxSinkReconhecimento implements Consumer<FluxSink<Reconhecimento>> {

    private FluxSink<Reconhecimento> fluxSink;

    @Override
    public void accept(FluxSink<Reconhecimento> reconhecimentoFluxSink) {
        this.fluxSink = reconhecimentoFluxSink;
    }

    public void publishReconhecimento(Reconhecimento reconhecimento){
        this.fluxSink.next(reconhecimento);
    }
}
