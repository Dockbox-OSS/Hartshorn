package test.org.dockbox.hartshorn.inject.compatibility;

import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.test.TestApplicationCustomizer;

public class DisableSingleConstructorFallbackTestCustomizer implements TestApplicationCustomizer {

    @Override
    public void customizeEnvironment(ConfigurableApplicationEnvironment.Configurer configurer) {
        configurer.disallowFallbackToSingleConstructor();
    }
}
