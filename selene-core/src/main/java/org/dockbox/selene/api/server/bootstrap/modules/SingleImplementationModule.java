package org.dockbox.selene.api.server.bootstrap.modules;

import com.google.inject.AbstractModule;

public class SingleImplementationModule<T> extends AbstractModule
{
    private final Class<T> target;
    private final Class<? extends T> implementation;

    public SingleImplementationModule(Class<T> target, Class<? extends T> implementation)
    {
        this.target = target;
        this.implementation = implementation;
    }

    @Override
    protected void configure()
    {
        super.configure();
        this.bind(this.target).to(this.implementation);
    }
}
