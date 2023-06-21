package org.dockbox.hartshorn.inject.strategy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

public class MethodAwareBindingStrategyContext<T> extends DefaultApplicationAwareContext implements BindingStrategyContext<T> {

    private final ComponentContainer<T> componentContainer;
    private final MethodView<T, ?> method;

    public MethodAwareBindingStrategyContext(final ApplicationContext applicationContext, final ComponentContainer<T> componentContainer, final MethodView<T, ?> method) {
        super(applicationContext);
        this.componentContainer = componentContainer;
        this.method = method;
    }

    @Override
    public ComponentContainer<T> componentContainer() {
        return this.componentContainer;
    }
    
    public MethodView<T, ?> method() {
        return this.method;
    }
}
