package org.dockbox.hartshorn.di.services;

import org.dockbox.hartshorn.di.Activatable;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;

import java.lang.annotation.Annotation;

public interface ComponentProcessor<A extends Annotation> extends Activatable<A>, OrderedServiceHandler {

    boolean processable(ApplicationContext context, TypeContext<?> type);

    <T> void process(ApplicationContext context, TypeContext<T> type);
}
