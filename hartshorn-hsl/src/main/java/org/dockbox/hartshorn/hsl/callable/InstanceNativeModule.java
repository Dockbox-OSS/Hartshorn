package org.dockbox.hartshorn.hsl.callable;

import java.util.Objects;

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
