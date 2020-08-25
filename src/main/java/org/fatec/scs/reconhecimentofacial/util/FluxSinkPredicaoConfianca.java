package org.fatec.scs.reconhecimentofacial.util;

import org.fatec.scs.reconhecimentofacial.dto.auxiliar.PredicaoConfianca;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public class FluxSinkPredicaoConfianca implements Consumer<FluxSink<PredicaoConfianca>> {

    private FluxSink<PredicaoConfianca> fluxSink = EmitterProcessor.<PredicaoConfianca>create().sink();

    @Override
    public void accept(FluxSink<PredicaoConfianca> predicaoConfiancaFluxSink) {
        this.fluxSink = predicaoConfiancaFluxSink;
    }

    public void publishPredicaoConfianca(PredicaoConfianca predicaoConfianca){
        this.fluxSink.next(predicaoConfianca);
    }
}
