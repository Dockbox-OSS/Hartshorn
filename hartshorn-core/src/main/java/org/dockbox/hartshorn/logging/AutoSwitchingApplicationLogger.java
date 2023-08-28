/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        return context -> {
            if (!context.input().introspect("ch.qos.logback.classic.Logger").isVoid()) {
                return new LogbackApplicationLogger();
            }

            // If no specializations are available, fall back to configured default
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return configurer.defaultFallback.initialize(context);
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
