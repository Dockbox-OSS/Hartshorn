package org.dockbox.selene.api.server.bootstrap.modules;

import com.google.inject.AbstractModule;

public class SingleInstanceModule<T> extends AbstractModule
{
    private final Class<T> target;
    private final T instance;

    public SingleInstanceModule(Class<T> target, T instance)
    {
        this.target = target;
        this.instance = instance;
    }

    @Override
    protected void configure()
    {
        super.configure();
        this.bind(this.target).toInstance(this.instance);
    }
}
