package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.core.objects.optional.Exceptional;

@FunctionalInterface
public interface NonmodifingPipe<I> extends IPipe<I, I> {
    void execute(I input, Throwable throwable);

    @Override
    default I apply(Exceptional<I> input) {
        this.execute(input.get(), input.orElseExcept(null));
        return input.get();
    }

    static <I> NonmodifingPipe<I> of(NonmodifingPipe<I> pipe) {
        return pipe;
    }
}
