package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.integrated.data.pipeline.Pipeline;

@FunctionalInterface
public interface CancellablePipe<I, O> extends IPipe<I, O> {

    O execute(Runnable cancelPipeline, I input, Throwable throwable);

    @Override
    default O apply(Pipeline<I, O> pipeline, I input, Throwable throwable) {
        return this.execute(pipeline::cancel, input, throwable);
    }

    @Override
    default Class<CancellablePipe> getType() {
        return CancellablePipe.class;
    }

    @Override
    default String pipeName() {
        return "Cancelable Pipe";
    }

    static <I, O> CancellablePipe<I, O> of(CancellablePipe<I, O> pipe) {
        return pipe;
    }
}
