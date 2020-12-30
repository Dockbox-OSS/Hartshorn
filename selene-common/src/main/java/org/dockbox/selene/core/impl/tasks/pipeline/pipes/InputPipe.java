package org.dockbox.selene.core.impl.tasks.pipeline.pipes;

import org.dockbox.selene.core.objects.Exceptional;

@FunctionalInterface
public interface InputPipe<I, O> extends StandardPipe<I, O> {

    O execute (I input) throws Exception;

    @Override
    default O apply(Exceptional<I> input) throws Exception {
        return this.execute(input.orNull());
    }

    static <I, O> InputPipe<I, O> of(InputPipe<I, O> pipe) {
        return pipe;
    }
}
