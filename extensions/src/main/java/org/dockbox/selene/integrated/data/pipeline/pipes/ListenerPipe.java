package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.core.objects.Exceptional;

@FunctionalInterface
public interface ListenerPipe<I> extends StandardPipe<I, I> {

    void execute(I input) throws Exception;

    @Override
    default I apply(Exceptional<I> input) throws Exception {
        this.execute(input.orNull());
        return input.orNull();
    }

    static <I> ListenerPipe<I> of(ListenerPipe<I> pipe) {
        return pipe;
    }
}
