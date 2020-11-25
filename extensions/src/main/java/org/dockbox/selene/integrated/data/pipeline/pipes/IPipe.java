package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.core.objects.optional.Exceptional;

public interface IPipe<I, O> {

    O apply(Exceptional<I> input);

    //So that you can identify the type, if created through a lambda expression.
    default Class<? extends IPipe> getType() {
        return IPipe.class;
    }
}
