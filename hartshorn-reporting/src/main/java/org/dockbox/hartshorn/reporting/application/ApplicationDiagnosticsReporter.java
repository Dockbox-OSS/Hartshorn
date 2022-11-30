/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.reporting.application;

import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.Observer;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.NamedContext;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ApplicationDiagnosticsReporter implements ConfigurableDiagnosticsReporter<ApplicationReportingConfiguration>, CategorizedDiagnosticsReporter {

    private final ApplicationReportingConfiguration configuration = new ApplicationReportingConfiguration();

    private final ApplicationContext applicationContext;

    public ApplicationDiagnosticsReporter(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        if (this.configuration.includeVersion()) {
            collector.property("version").write(Hartshorn.VERSION);
        }
        if (this.configuration.includeJarLocation()) {
            reportJarLocation(collector);
        }
        if (this.configuration.includeApplicationProperties()) {
            this.reportApplicationProperties(collector);
        }
        if (this.configuration.includeServiceActivators()) {
            this.reportServiceActivators(collector);
        }
        if (this.configuration.includeObservers()) {
            this.reportObservers(collector);
        }
        if (this.configuration.includeContexts()) {
            this.reportContexts(collector);
        }
    }

    private static void reportJarLocation(final DiagnosticsPropertyCollector collector) {
        String jarLocation;
        try {
            jarLocation = Hartshorn.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString();
        } catch (final Exception e) {
            jarLocation = "Unknown";
        }
        collector.property("jar").write(jarLocation);
    }

    private void reportApplicationProperties(final DiagnosticsPropertyCollector collector) {
        final Properties properties = this.applicationContext.properties();
        final Reportable reporter = new PropertiesReporter(properties);
        collector.property("properties").write(reporter);
    }

    private void reportServiceActivators(final DiagnosticsPropertyCollector collector) {
        final String[] activators = this.applicationContext.activators().stream()
                .map(a -> a.annotationType().getCanonicalName())
                .toArray(String[]::new);
        collector.property("activators").write(activators);
    }

    private void reportObservers(final DiagnosticsPropertyCollector collector) {
        final ApplicationEnvironment environment = this.applicationContext.environment();
        if (environment instanceof ObservableApplicationEnvironment observable) {
            final Map<Class<? extends Observer>, List<Observer>> observers = observable.observers(Observer.class).stream()
                            .collect(Collectors.groupingBy(Observer::getClass));

            collector.property("observers").write(c -> {
                for (final Class<? extends Observer> observerType : observers.keySet()) {
                    c.property(observerType.getSimpleName()).write(observers.get(observerType).size());
                }
            });
        }
    }

    private void reportContexts(final DiagnosticsPropertyCollector collector) {
        final AtomicReference<BiConsumer<DiagnosticsPropertyCollector, Context>> reporterReference = new AtomicReference<>();

        final BiConsumer<DiagnosticsPropertyCollector, Context> reporter = (contextsCollector, context) -> {
            contextsCollector.property("type").write(context.getClass().getCanonicalName());
            if (context instanceof Reportable reportable) {
                contextsCollector.property("data").write(reportable);
            }

            if (context instanceof NamedContext namedContext) contextsCollector.property("name").write(namedContext.name());
            if (!context.all().isEmpty()) {
                final Reportable[] childReporters = childReporters(reporterReference, context);
                contextsCollector.property("children").write(childReporters);
            }
        };
        reporterReference.set(reporter);

        final Reportable[] reporters = childReporters(reporterReference, this.applicationContext);
        collector.property("contexts").write(reporters);
    }

    @NotNull
    private static Reportable[] childReporters(final AtomicReference<BiConsumer<DiagnosticsPropertyCollector, Context>> reporterReference, final Context context) {
        return context.all().stream()
                .map(c -> (Reportable) (contextsController -> reporterReference.get().accept(contextsController, c)))
                .toArray(Reportable[]::new);
    }

    @Override
    public ApplicationReportingConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public String category() {
        return "application";
    }
}
