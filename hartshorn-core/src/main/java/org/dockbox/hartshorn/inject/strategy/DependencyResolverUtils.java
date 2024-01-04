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

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPoint;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;
import java.util.stream.Collectors;

public final class DependencyResolverUtils {

    private DependencyResolverUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Set<ComponentKey<?>> resolveDependencies(
            TypeView<?> type,
            ApplicationEnvironment environment
    ) {
        Set<? extends ComponentInjectionPoint<?>> points = environment.injectionPointsResolver().resolve(type);
        return points.stream()
                .map(ComponentInjectionPoint::declaration)
                .map(environment.componentKeyResolver()::resolve)
                .collect(Collectors.toSet());
    }

    public static Set<ComponentKey<?>> resolveDependencies(
            ExecutableElementView<?> executable,
            ApplicationEnvironment environment
    ) {
        return executable.parameters().all().stream()
                .filter(parameter -> !parameter.type().isChildOf(Context.class))
                .map(environment.componentKeyResolver()::resolve)
                .collect(Collectors.toSet());
    }
}
