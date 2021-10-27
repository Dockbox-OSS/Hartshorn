package org.dockbox.hartshorn.di.services;

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;

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
