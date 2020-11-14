package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.optional.Exceptional;

@FunctionalInterface
public interface ExceptionalPipe<I, O> extends IPipe<I, O>{

    O execute(Exceptional<I> exceptional);

    @Override
    default O apply(Pipeline<I, O> pipeline, I input, Throwable throwable) {
        return this.execute(Exceptional.ofNullable(input, throwable));
    }

    @Override
    default String pipeName() {
        return "Exceptional Pipe";
    }

    static <I, O> ExceptionalPipe<I, O> of(ExceptionalPipe<I, O> pipe) {
        return pipe;
    }
}
