package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.launchpad.SimpleApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationBuilder;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationContextFactory;

import java.util.List;

public class CompositeTestApplicationCustomizer implements TestApplicationCustomizer {

    private final List<TestApplicationCustomizer> customizers;

    public CompositeTestApplicationCustomizer(List<TestApplicationCustomizer> customizers) {
        this.customizers = customizers;
    }

    @Override
    public void customizeBuilder(StandardApplicationBuilder.Configurer configurer) {
        this.customizers.forEach(customizer -> customizer.customizeBuilder(configurer));
    }

    @Override
    public void customizeEnvironment(ConfigurableApplicationEnvironment.Configurer configurer) {
        this.customizers.forEach((customizer) -> customizer.customizeEnvironment(configurer));
    }

    @Override
    public void customizeFactory(StandardApplicationContextFactory.Configurer configurer) {
        this.customizers.forEach((customizer) -> customizer.customizeFactory(configurer));
    }

    @Override
    public void customizeApplication(SimpleApplicationContext.Configurer configurer) {
        this.customizers.forEach((customizer) -> customizer.customizeApplication(configurer));
    }
}
