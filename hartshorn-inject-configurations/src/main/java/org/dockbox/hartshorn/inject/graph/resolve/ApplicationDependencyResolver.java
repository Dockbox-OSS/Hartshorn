/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.graph.CompositeDependencyResolver;
import org.dockbox.hartshorn.inject.graph.DependencyResolver;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.configure.StreamableConfigurer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Standard {@link DependencyResolver} for injection-capable applications. This resolver allows for the resolution
 * of dependencies from managed components and binding methods, depending on the configuration of this resolver.
 *
 * <p>Additional dependency resolvers can be added to this resolver using the {@link Configurer}, though it is
 * recommended to use a separate {@link DependencyResolver} for custom resolvers, as this resolver is intended
 * to be used for standard application dependencies.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ApplicationDependencyResolver extends CompositeDependencyResolver {

    public ApplicationDependencyResolver(Set<DependencyResolver> resolvers) {
        super(resolvers);
    }

    public static ContextualInitializer<InjectionCapableApplication, DependencyResolver> create(Customizer<Configurer> customizer) {
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
     * Configurer for the {@link ApplicationDependencyResolver}.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer extends StreamableConfigurer<InjectionCapableApplication, DependencyResolver> {

        public Configurer withManagedComponents() {
            ContextualInitializer<InjectionCapableApplication, DependencyResolver> methodDependencyResolver = ContextualInitializer.of(application -> {
                return new ComponentDependencyResolver(application.environment(), application.defaultBinder());
            });
            this.add(methodDependencyResolver);
            return this;
        }

        public Configurer withBindsMethods(Customizer<ManagedConfigurationDependencyResolver.Configurer> customizer) {
            ContextualInitializer<InjectionCapableApplication, DependencyResolver> methodDependencyResolver = ManagedConfigurationDependencyResolver.create(customizer);
            this.add(methodDependencyResolver);
            return this;
        }
    }
}
