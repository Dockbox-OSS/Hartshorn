package org.dockbox.selene.integrated.data.pipeline.pipes;

@FunctionalInterface
public interface ExceptionalPipe<I, O> extends IPipe<I, O> {

    static <I, O> ExceptionalPipe<I, O> of(ExceptionalPipe<I, O> pipe) {
        return pipe;
    }
}
