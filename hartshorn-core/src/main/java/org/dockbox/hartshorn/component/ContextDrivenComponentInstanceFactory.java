package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.binding.ComponentInstanceFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;

public class ContextDrivenComponentInstanceFactory implements ComponentInstanceFactory {

    private final ApplicationContext applicationContext;

    public ContextDrivenComponentInstanceFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> Option<ObjectContainer<T>> instantiate(ComponentKey<T> key) throws ApplicationException {
        return new ContextDrivenProvider<>(key).provide(applicationContext);
    }
}
