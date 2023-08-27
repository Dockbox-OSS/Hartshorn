package org.dockbox.hartshorn.logging;

import org.dockbox.hartshorn.application.ApplicationConfigurer;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.logging.logback.LogbackApplicationLogger;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.ContextualInitializer;

public final class AutoSwitchingApplicationLogger {

    private AutoSwitchingApplicationLogger() {
        throw new UnsupportedOperationException();
    }

    public static ContextualInitializer<ApplicationEnvironment, ApplicationLogger> create(Customizer<Configurer> customizer) {
        return environment -> {
            if (!environment.introspect("ch.qos.logback.classic.Logger").isVoid()) {
                return new LogbackApplicationLogger();
            }

            // If no specializations are available, fall back to configured default
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return configurer.defaultFallback.initialize(environment);
        };
    }

    public static class Configurer extends ApplicationConfigurer {

        private ContextualInitializer<ApplicationEnvironment, ApplicationLogger> defaultFallback = ContextualInitializer.of(Slf4jApplicationLogger::new);

        public Configurer defaultFallback(ApplicationLogger defaultFallback) {
            return this.defaultFallback(ContextualInitializer.of(defaultFallback));
        }

        public Configurer defaultFallback(ContextualInitializer<ApplicationEnvironment, ApplicationLogger> defaultFallback) {
            this.defaultFallback = defaultFallback;
            return this;
        }
    }
}
