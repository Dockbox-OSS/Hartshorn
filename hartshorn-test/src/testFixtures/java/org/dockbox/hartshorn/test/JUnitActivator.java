package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.ApplicationBootstrap;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.di.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.di.annotations.inject.InjectPhase;
import org.dockbox.hartshorn.test.util.JUnitInjector;

import java.lang.annotation.Annotation;

public class JUnitActivator implements Activator {
    @Override
    public Class<? extends ApplicationBootstrap> value() {
        return JUnitBootstrap.class;
    }

    @Override
    public String prefix() {
        return Hartshorn.PACKAGE_PREFIX;
    }

    @Override
    public InjectConfig[] configs() {
        return new InjectConfig[] { new InjectConfig() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return InjectConfig.class;
            }

            @Override
            public Class<? extends InjectConfiguration> value() {
                return JUnitInjector.class;
            }

            @Override
            public InjectPhase phase() {
                return InjectPhase.EARLY;
            }
        } };
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Activator.class;
    }
}
