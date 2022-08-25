package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.AutoCreating;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.inject.Inject;

@AutoCreating
public class BeanContext extends DefaultApplicationAwareContext implements BeanCollector {

    private final List<BeanReference<?>> beans = new CopyOnWriteArrayList<>();

    @Inject
    public BeanContext(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public BeanProvider provider() {
        return new ContextBeanProvider(this);
    }

    public List<BeanReference<?>> beans() {
        return this.beans;
    }

    @Override
    public <T> BeanReference<T> register(final T bean, final TypeContext<T> type, final String id) {
        final BeanReference<T> beanReference = new BeanReference<>(bean, type, id);
        this.beans.add(beanReference);
        return beanReference;
    }

    @Override
    public void unregister(final BeanReference<?> beanReference) {
        this.beans.remove(beanReference);
    }
}
