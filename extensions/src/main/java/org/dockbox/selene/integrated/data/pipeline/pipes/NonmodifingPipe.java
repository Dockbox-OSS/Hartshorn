package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.integrated.data.pipeline.Pipeline;

@FunctionalInterface
public interface NonmodifingPipe<I> extends IPipe<I, I> {
    void execute(I input, Throwable throwable);

    @Override
    default I apply(Pipeline<I, I> pipeline, I input, Throwable throwable) {
        this.execute(input, throwable);
        return input;
    }

    static <I> NonmodifingPipe<I> of(NonmodifingPipe<I> pipe) {
        return pipe;
    }
}
