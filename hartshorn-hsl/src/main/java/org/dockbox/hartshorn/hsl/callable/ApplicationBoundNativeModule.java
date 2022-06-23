package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.application.context.ApplicationContext;

public class ApplicationBoundNativeModule extends AbstractNativeModule {

    private final Class<?> moduleClass;
    private final ApplicationContext applicationContext;

    public ApplicationBoundNativeModule(final Class<?> moduleClass, final ApplicationContext applicationContext) {
        this.moduleClass = moduleClass;
        this.applicationContext = applicationContext;
    }

    @Override
    protected Class<?> moduleClass() {
        return this.moduleClass;
    }

    @Override
    protected Object instance() {
        return this.applicationContext.get(this.moduleClass);
    }
}
