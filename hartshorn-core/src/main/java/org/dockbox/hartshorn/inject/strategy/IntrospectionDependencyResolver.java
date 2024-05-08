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

package org.dockbox.hartshorn.inject.strategy;

import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPoint;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class IntrospectionDependencyResolver {

    private final ApplicationEnvironment environment;

    public IntrospectionDependencyResolver(ApplicationEnvironment environment) {
        this.environment = environment;
    }

    public Set<ComponentKey<?>> resolveDependencies(TypeView<?> type) {
        Set<? extends ComponentInjectionPoint<?>> points = this.environment.injectionPointsResolver().resolve(type);
        return points.stream()
                .map(ComponentInjectionPoint::declaration)
                .map(this.environment.componentKeyResolver()::resolve)
                .collect(Collectors.toSet());
    }

    public Set<ComponentKey<?>> resolveDependencies(ExecutableElementView<?> executable) {
        return executable.parameters().all().stream()
                .filter(parameter -> !parameter.type().isChildOf(ContextView.class))
                .map(this.environment.componentKeyResolver()::resolve)
                .collect(Collectors.toSet());
    }
}
