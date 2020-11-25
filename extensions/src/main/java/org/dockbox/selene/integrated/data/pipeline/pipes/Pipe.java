package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.core.objects.optional.Exceptional;

@FunctionalInterface
public interface Pipe<I, O> extends IPipe<I, O> {

    O execute(I input, Throwable throwable);

    @Override
    default O apply(Exceptional<I> input) {
        return this.execute(input.orElse(null), input.orElseExcept(null));
    }

    static <I, O> Pipe<I, O> of(Pipe<I, O> pipe) {
        return pipe;
    }
}