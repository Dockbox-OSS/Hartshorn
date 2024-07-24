package org.dockbox.hartshorn.test;

import java.util.Properties;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ApplicationPropertyHolder;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.InjectorEnvironment;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.util.option.Option;

public class TestInjectionCapableApplication
        extends DefaultContext
        implements InjectionCapableApplication {

    private final InjectorEnvironment environment = new TestInjectorEnvironment();
    private final ComponentProvider provider = null; // TODO: Nothing in hartshorn-inject, need to implement
    private final HierarchicalBinder binder = null; // TODO: Nothing in hartshorn-inject, need to implement

    @Override
    public InjectorEnvironment environment() {
        return this.environment;
    }

    @Override
    public ComponentProvider defaultProvider() {
        return null;
    }

    @Override
    public HierarchicalBinder defaultBinder() {
        return null;
    }

    @Override
    public ApplicationPropertyHolder properties() {
        // TODO: JDKPropertiesPropertyHolder?
        return new ApplicationPropertyHolder() {

            @Override
            public Properties properties() {
                return new Properties();
            }

            @Override
            public Option<String> property(String key) {
                return Option.empty();
            }
        };
    }
}
