package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.integrated.data.pipeline.Pipeline;

@FunctionalInterface
public interface Pipe<I, O> extends IPipe<I, O> {

    O execute(I input, Throwable throwable);

    @Override
    default O apply(Pipeline<I, O> pipeline, I input, Throwable throwable) {
        return this.execute(input, throwable);
    }

    static <I, O> Pipe<I, O> of(Pipe<I, O> pipe) {
        return pipe;
    }
}