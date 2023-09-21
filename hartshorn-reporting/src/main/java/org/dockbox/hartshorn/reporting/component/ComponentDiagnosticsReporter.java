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

package org.dockbox.hartshorn.reporting.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.component.ComponentReportingConfiguration.ComponentAttribute;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentDiagnosticsReporter implements ConfigurableDiagnosticsReporter<ComponentReportingConfiguration>, CategorizedDiagnosticsReporter {

    public static final String COMPONENTS_CATEGORY = "components";

    private final ComponentReportingConfiguration configuration = new ComponentReportingConfiguration();
    private final ApplicationContext applicationContext;

    public ComponentDiagnosticsReporter(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        final ComponentLocator componentLocator = this.applicationContext.get(ComponentLocator.class);

        if (this.configuration.groupBy() == ComponentAttribute.NONE) {
            final Reportable[] reporters = this.diagnosticsReporters(componentLocator.containers());
            collector.property("all").write(reporters);
        }
        else {
            final Map<String, List<ComponentContainer<?>>> groupedContainers = componentLocator.containers().stream()
                    .collect(Collectors.groupingBy(container -> switch (this.configuration.groupBy()) {
                        case STEREOTYPE -> stereotype(container).getCanonicalName();
                        case PACKAGE -> container.type().packageInfo().name();
                        default -> throw new IllegalStateException("Unexpected value: " + this.configuration.groupBy());
                    }));

            for (final String key : groupedContainers.keySet()) {
                collector.property(key).write(this.diagnosticsReporters(groupedContainers.get(key)));
            }
        }
    }

    @NonNull
    private Reportable[] diagnosticsReporters(final Collection<ComponentContainer<?>> containers) {
        return containers.stream()
                .map(container -> (Reportable) new ComponentContainerReporter(this, container))
                .toArray(Reportable[]::new);
    }

    static Class<?> stereotype(final ComponentContainer<?> container) {
        final Component component = container.type().annotations().get(Component.class).get();
        return component.annotationType();
    }

    @Override
    public ComponentReportingConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public String category() {
        return COMPONENTS_CATEGORY;
    }
}
