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

package org.dockbox.hartshorn.inject;

import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.inject.graph.DependencyResolver;
import org.dockbox.hartshorn.inject2.environment.InjectorEnvironment;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.StreamableConfigurer;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ApplicationDependencyResolver extends CompositeDependencyResolver {

    public ApplicationDependencyResolver(Set<DependencyResolver> resolvers) {
        super(resolvers);
    }

    public static ContextualInitializer<InjectorEnvironment, DependencyResolver> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer()
                    .withManagedComponents()
                    .withBindsMethods(Customizer.useDefaults());

            customizer.configure(configurer);

            Set<DependencyResolver> resolvers = configurer.stream()
                    .map(initializer -> initializer.initialize(context))
                    .collect(Collectors.toSet());

            return new ApplicationDependencyResolver(resolvers);
        };
    }

    /**
     * TODO: #1060 Add documentation
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer extends StreamableConfigurer<InjectorEnvironment, DependencyResolver> {

        public Configurer withManagedComponents() {
            ContextualInitializer<InjectorEnvironment, DependencyResolver> methodDependencyResolver = ContextualInitializer.of(ComponentDependencyResolver::new);
            this.add(methodDependencyResolver);
            return this;
        }

        public Configurer withBindsMethods(Customizer<BindsMethodDependencyResolver.Configurer> customizer) {
            ContextualInitializer<InjectorEnvironment, DependencyResolver> methodDependencyResolver = BindsMethodDependencyResolver.create(customizer);
            this.add(methodDependencyResolver);
            return this;
        }
    }
}
