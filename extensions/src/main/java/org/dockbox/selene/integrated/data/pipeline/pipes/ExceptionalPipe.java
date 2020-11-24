package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.pipeline.Pipeline;

@FunctionalInterface
public interface ExceptionalPipe<I, O> extends IPipe<I, O> {

    @Override
    default String pipeName() {
        return "Exceptional Pipe";
    }

    static <I, O> ExceptionalPipe<I, O> of(ExceptionalPipe<I, O> pipe) {
        return pipe;
    }
}
