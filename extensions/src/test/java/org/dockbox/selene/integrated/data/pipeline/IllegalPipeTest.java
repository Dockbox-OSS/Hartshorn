package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;

public interface IllegalPipeTest<I, O> extends IPipe<I, O> {

    O apply (I input);

    static <I, O> IllegalPipeTest<I, O> of(IllegalPipeTest<I, O> pipe) {
        return pipe;
    }
}
