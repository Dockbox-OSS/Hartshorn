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

package org.dockbox.hartshorn.inject.graph.resolve;

import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentKeyResolver;
import org.dockbox.hartshorn.inject.targets.ComponentInjectionPoint;
import org.dockbox.hartshorn.inject.targets.ComponentInjectionPointsResolver;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A resolver for dependencies of components based on introspection. For types, it resolves the
 * dependencies by inspecting the injection points of the component. For executable elements, it resolves
 * the dependencies by inspecting the parameters of the executable element, excluding any context parameters.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class IntrospectionDependencyResolver {

    private final ComponentInjectionPointsResolver injectionPointsResolver;
    private final ComponentKeyResolver componentKeyResolver;

    public IntrospectionDependencyResolver(ComponentInjectionPointsResolver injectionPointsResolver, ComponentKeyResolver componentKeyResolver) {
        this.injectionPointsResolver = injectionPointsResolver;
        this.componentKeyResolver = componentKeyResolver;
    }

    /**
     * Resolves the dependencies of a given type by inspecting its injection points, using the configured
     * {@link ComponentInjectionPointsResolver}.
     *
     * @param type the type for which to resolve dependencies
     * @return a set of component keys representing the dependencies of the type
     */
    public Set<ComponentKey<?>> resolveDependencies(TypeView<?> type) {
        Set<? extends ComponentInjectionPoint<?>> points = this.injectionPointsResolver.resolve(type);
        return points.stream()
                .map(ComponentInjectionPoint::declaration)
                .map(this.componentKeyResolver::resolve)
                .collect(Collectors.toSet());
    }

    /**
     * Resolves the dependencies of a given executable element by inspecting its parameters, excluding any
     * context parameters. The resolved dependencies are returned as a set of component keys.
     *
     * @param executable the executable element for which to resolve dependencies
     * @return a set of component keys representing the dependencies of the executable element
     */
    public Set<ComponentKey<?>> resolveDependencies(ExecutableElementView<?> executable) {
        return executable.parameters().all().stream()
                .filter(parameter -> !parameter.type().isChildOf(ContextView.class))
                .map(this.componentKeyResolver::resolve)
                .collect(Collectors.toSet());
    }
}
