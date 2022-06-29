package org.dockbox.hartshorn.hsl.callable.module;

import java.util.Objects;

/**
 * Implementation of {@link AbstractNativeModule} which provides the instance of the module
 * based on a predefined instance.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class InstanceNativeModule extends AbstractNativeModule {

    private final Class<?> moduleClass;
    private final Object instance;

    public InstanceNativeModule(final Object instance) {
        this.instance = Objects.requireNonNull(instance);
        this.moduleClass = instance.getClass();
    }

    @Override
    protected Class<?> moduleClass() {
        return this.moduleClass;
    }

    @Override
    protected Object instance() {
        return this.instance;
    }
}
