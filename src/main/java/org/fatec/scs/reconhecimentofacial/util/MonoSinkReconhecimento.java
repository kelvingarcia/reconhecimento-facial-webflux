package org.fatec.scs.reconhecimentofacial.util;

import org.fatec.scs.reconhecimentofacial.dto.auxiliar.Reconhecimento;
import reactor.core.publisher.MonoSink;

import java.util.function.Consumer;

public class MonoSinkReconhecimento implements Consumer<MonoSink<Reconhecimento>> {
    private MonoSink<Reconhecimento> monoSink;

    @Override
    public void accept(MonoSink<Reconhecimento> reconhecimentoMonoSink) {
        this.monoSink = reconhecimentoMonoSink;
    }

    public void publishReconhecimento(Reconhecimento reconhecimento){
        this.monoSink.success(reconhecimento);
    }
}
