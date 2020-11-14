package org.dockbox.selene.integrated.data.pipeline;

@FunctionalInterface
public interface IPipe<I, O> {

    O apply(Pipeline<I, O> pipeline, I input, Throwable throwable);

    default String pipeName() {
        return this.getClass().getSimpleName();
    }
}
