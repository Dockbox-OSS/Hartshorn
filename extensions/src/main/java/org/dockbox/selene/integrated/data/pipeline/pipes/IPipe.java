package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.integrated.data.pipeline.Pipeline;

public interface IPipe<I, O> {

    O apply(Pipeline<I, O> pipeline, I input, Throwable throwable);

    //So that you can identify the type, if created through a lambda expression.
    default Class<? extends IPipe> getType() {
        return this.getClass();
    }

    default String pipeName() {
        return this.getClass().getSimpleName();
    }
}
