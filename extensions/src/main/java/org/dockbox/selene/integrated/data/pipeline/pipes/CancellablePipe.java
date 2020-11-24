package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.core.objects.optional.Exceptional;

@FunctionalInterface
public interface CancellablePipe<I, O> extends IPipe<I, O> {

    O execute(Runnable cancelPipeline, I input, Throwable throwable);

    @Override
    default O apply(Exceptional<I> input) {
        return this.execute(null, input.get(), input.orElseExcept(null));
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
