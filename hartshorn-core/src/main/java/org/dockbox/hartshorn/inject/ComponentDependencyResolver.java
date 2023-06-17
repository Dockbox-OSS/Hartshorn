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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;

public class ComponentDependencyResolver extends AbstractExecutableElementDependencyResolver {

    @Override
    protected Set<DependencyContext<?>> resolveSingle(final ComponentContainer componentContainer, final ApplicationContext applicationContext) throws DependencyResolutionException {
        final TypeView<?> type = componentContainer.type();
        final ConstructorView<?> constructorView = CyclingConstructorAnalyzer.findConstructor(type)
                .mapError(DependencyResolutionException::new)
                .rethrow()
                .orNull();

        if (constructorView == null) {
            return Set.of();
        }
        final Set<ComponentKey<?>> dependencies = this.resolveDependencies(constructorView);

        final ComponentKey<?> componentKey = ComponentKey.of(type.type());
        return Set.of(new ManagedComponentDependencyContext<>(componentKey, dependencies));
    }
}
