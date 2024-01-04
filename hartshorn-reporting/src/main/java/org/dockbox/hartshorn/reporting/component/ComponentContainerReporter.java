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

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.condition.RequiresCondition;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPointsResolver;
import org.dockbox.hartshorn.inject.ComponentKeyResolver;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.component.ComponentReportingConfiguration.ComponentAttribute;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.FieldView;

import java.util.List;
import java.util.Map.Entry;

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

        String componentType = StringUtilities.capitalize(this.container.componentType().name()
                .toLowerCase());

        componentCollector.property("componentType").write(componentType);
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

        for (Entry<ComponentAttribute, Boolean> entry : this.componentDiagnosticsReporter.configuration().attributes()
                .entrySet()) {
            if (entry.getValue()) {
                switch (entry.getKey()) {
                    case STEREOTYPE -> componentCollector.property("stereotype")
                            .write(ComponentDiagnosticsReporter.stereotype(this.container)
                                    .getCanonicalName());
                    case PACKAGE -> componentCollector.property("package")
                            .write(this.container.type().packageInfo());
                }
            }
        }
    }

    private void reportAnnotatedField(DiagnosticsPropertyCollector injectCollector) {
        ComponentInjectionPointsResolver injectionPointsResolver = this.environment.injectionPointsResolver();
        ComponentKeyResolver componentKeyResolver = this.environment.componentKeyResolver();
        List<? extends FieldView<?, ?>> injectFields = this.container.type().fields()
                .all().stream()
                .filter(injectionPointsResolver::isInjectable)
                .toList();

        for (FieldView<?, ?> injectField : injectFields) {
            injectCollector.property(injectField.name()).write(fieldCollector -> {
                ComponentKey<?> key = componentKeyResolver.resolve(injectField);
                fieldCollector.property("key").write(key);
            });
        }
    }
}
