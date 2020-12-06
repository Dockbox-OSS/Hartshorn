package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.core.objects.optional.Exceptional;

@FunctionalInterface
public interface InputPipe<I, O> extends IPipe<I, O> {

    O execute (I input);

    @Override
    default O apply(Exceptional<I> input) {
        return this.execute(input.orNull());
    }

    static <I, O> InputPipe<I, O> of(InputPipe<I, O> pipe) {
        return pipe;
    }
}
