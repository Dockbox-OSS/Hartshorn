package org.dockbox.hartshorn.logging;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.logging.logback.LogbackApplicationLogger;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyInitializer;

public final class AutoSwitchingApplicationLogger {

    private AutoSwitchingApplicationLogger() {
        throw new UnsupportedOperationException();
    }

    public static LazyInitializer<ApplicationEnvironment, ApplicationLogger> create(Customizer<Configurer> customizer) {
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

    public static class Configurer {

        private LazyInitializer<ApplicationEnvironment, ApplicationLogger> defaultFallback = LazyInitializer.of(Slf4jApplicationLogger::new);

        public Configurer defaultFallback(ApplicationLogger defaultFallback) {
            return this.defaultFallback(LazyInitializer.of(defaultFallback));
        }

        public Configurer defaultFallback(LazyInitializer<ApplicationEnvironment, ApplicationLogger> defaultFallback) {
            this.defaultFallback = defaultFallback;
            return this;
        }
    }
}
