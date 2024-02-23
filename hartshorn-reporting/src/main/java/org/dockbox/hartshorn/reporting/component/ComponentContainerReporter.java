/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.Set;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.condition.RequiresCondition;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPoint;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPointsResolver;
import org.dockbox.hartshorn.inject.ComponentKeyResolver;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;

/**
 * A reportable that reports the contents of a {@link ComponentContainer} instance. This includes the following information:
 * <ul>
 *     <li>Component class type, {@link ComponentContainer#type()}</li>
 *     <li>Component ID, {@link ComponentContainer#id()}</li>
 *     <li>Component name, {@link ComponentContainer#name()}</li>
 *     <li>Component singleton status, {@link ComponentContainer#singleton()}</li>
 *     <li>Component lazy status, {@link ComponentContainer#lazy()}</li>
 *     <li>Whether the component permits proxying, {@link ComponentContainer#permitsProxying()}</li>
 *     <li>Whether the component permits processing, {@link ComponentContainer#permitsProcessing()}</li>
 *     <li>Component stereotype, {@link ComponentDiagnosticsReporter#stereotype(ComponentContainer)}</li>
 *     <li>Conditions that are required for the component to be active</li>
 *     <li>Dependencies of the component. Dependencies are resolved according to the rules defined by the active environment.</li>
 * </ul>
 *
 * <p>Each of these can be enabled or disabled individually, using the {@link ComponentReportingConfiguration} that
 * is provided by this reporter.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
class ComponentContainerReporter implements Reportable {

    private final ComponentDiagnosticsReporter componentDiagnosticsReporter;
    private final ApplicationEnvironment environment;
    private final ComponentContainer<?> container;

    public ComponentContainerReporter(ComponentDiagnosticsReporter componentDiagnosticsReporter,
                                      ApplicationEnvironment environment,
                                      ComponentContainer<?> container) {
        this.componentDiagnosticsReporter = componentDiagnosticsReporter;
        this.environment = environment;
        this.container = container;
    }

    @Override
    public void report(DiagnosticsPropertyCollector componentCollector) {
        componentCollector.property("type").write(this.container.type());
        componentCollector.property("id").write(this.container.id());
        componentCollector.property("name").write(this.container.name());
        componentCollector.property("singleton").write(this.container.singleton());
        componentCollector.property("lazy").write(this.container.lazy());
        componentCollector.property("permitsProxying").write(this.container.permitsProxying());
        componentCollector.property("permitsProcessing").write(this.container.permitsProcessing());

        if (this.componentDiagnosticsReporter.configuration().includeDependencies()) {
            componentCollector.property("dependencies").write(dependencyCollector -> {
                dependencyCollector.property("inject").write(this::reportAnnotatedField);
            });
        }

        if (this.componentDiagnosticsReporter.configuration().includeRequiredConditions()) {
            Reportable[] reporters = this.container.type().annotations().all(RequiresCondition.class)
                    .stream()
                    .map(requiresCondition -> (Reportable) conditionCollector -> {
                        conditionCollector.property("type").write(requiresCondition.annotationType().getCanonicalName());
                        conditionCollector.property("condition").write(requiresCondition.condition().getCanonicalName());
                        conditionCollector.property("failOnNoMatch").write(requiresCondition.failOnNoMatch());
                    }).toArray(Reportable[]::new);
            componentCollector.property("conditions").write(reporters);
        }

        componentCollector.property("stereotype").write(ComponentDiagnosticsReporter.stereotype(this.container).getCanonicalName());
        componentCollector.property("package").write(this.container.type().packageInfo());
    }

    private void reportAnnotatedField(DiagnosticsPropertyCollector injectCollector) {
        ComponentInjectionPointsResolver injectionPointsResolver = this.environment.injectionPointsResolver();
        ComponentKeyResolver componentKeyResolver = this.environment.componentKeyResolver();
        Set<? extends ComponentInjectionPoint<?>> injectionPoints = injectionPointsResolver.resolve(this.container.type());

        for (ComponentInjectionPoint<?> injectionPoint : injectionPoints) {
            injectCollector.property(injectionPoint.declaration().name()).write(fieldCollector -> {
                ComponentKey<?> key = componentKeyResolver.resolve(injectionPoint.declaration());
                fieldCollector.property("key").write(key);
            });
        }
    }
}
