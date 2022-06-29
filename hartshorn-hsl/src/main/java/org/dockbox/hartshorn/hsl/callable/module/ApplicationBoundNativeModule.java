package org.dockbox.hartshorn.hsl.callable.module;

import org.dockbox.hartshorn.application.context.ApplicationContext;

/**
 * Implementation of {@link AbstractNativeModule} which provides the instance of the module
 * using the {@link ApplicationContext}. The instance is lazy loaded, and only created when
 * it is accessed for the first time.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class ApplicationBoundNativeModule extends AbstractNativeModule {

    private final Class<?> moduleClass;
    private final ApplicationContext applicationContext;

    private Object instance;

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
        if (this.instance == null) {
            this.instance = this.applicationContext.get(this.moduleClass);
        }
        return this.instance;
    }
}
