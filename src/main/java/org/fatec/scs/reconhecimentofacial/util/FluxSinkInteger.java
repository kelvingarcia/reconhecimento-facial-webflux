package org.fatec.scs.reconhecimentofacial.util;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public class FluxSinkInteger implements Consumer<FluxSink<Integer>> {

    private FluxSink<Integer> fluxSink = EmitterProcessor.<Integer>create().sink();

    @Override
    public void accept(FluxSink<Integer> integerFluxSink) {
        this.fluxSink = integerFluxSink;
    }

    public void publishInteger(Integer integer){
        this.fluxSink.next(integer);
    }
}
