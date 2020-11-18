package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.integrated.data.pipeline.Pipeline;

@FunctionalInterface
public interface CancelablePipe<I, O> extends IPipe<I, O> {

    O execute(Runnable cancelPipeline, I input, Throwable throwable);

    @Override
    default O apply(Pipeline<I, O> pipeline, I input, Throwable throwable) {
        return this.execute(pipeline::cancel, input, throwable);
    }

    @Override
    default String pipeName() {
        return "Cancelable Pipe";
    }

    static <I, O> CancelablePipe<I, O> of(CancelablePipe<I, O> pipe) {
        return pipe;
    }
}
