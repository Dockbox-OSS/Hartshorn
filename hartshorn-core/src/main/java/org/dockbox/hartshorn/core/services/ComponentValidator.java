package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.annotation.Annotation;

public abstract class ComponentValidator<A extends Annotation> implements ComponentProcessor<A> {

    @Override
    public boolean processable(final ApplicationContext context, final TypeContext<?> type) {
        return true;
    }

    @Override
    public final ServiceOrder order() {
        return ServiceOrder.FIRST;
    }
}
