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

import org.dockbox.hartshorn.application.DefaultBindingConfigurerContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.strategy.BindingStrategyRegistry;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.StreamableConfigurer;

import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationDependencyResolver extends CompositeDependencyResolver {

    public ApplicationDependencyResolver(Set<DependencyResolver> resolvers) {
        super(resolvers);
    }

    public static ContextualInitializer<ApplicationContext, DependencyResolver> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer()
                    .withManagedComponents()
                    .withBindsMethods(Customizer.useDefaults());

            customizer.configure(configurer);

            ConditionMatcher conditionMatcher = configurer.conditionMatcher.initialize(context);
            Set<DependencyResolver> resolvers = configurer.stream()
                    .map(initializer -> initializer.initialize(context.transform(conditionMatcher)))
                    .collect(Collectors.toSet());

            DefaultBindingConfigurerContext.compose(context, binder -> {
                binder.bind(ConditionMatcher.class).singleton(conditionMatcher);
            });

            return new CompositeDependencyResolver(resolvers);
        };
    }

    public static class Configurer extends StreamableConfigurer<ConditionMatcher, DependencyResolver> {

        private ContextualInitializer<ApplicationContext, ConditionMatcher> conditionMatcher = context -> new ConditionMatcher(context.input());

        public Configurer withManagedComponents() {
            this.add(new ComponentDependencyResolver());
            return this;
        }

        public Configurer withBindsMethods(Customizer<BindingStrategyRegistry> customizer) {
            ContextualInitializer<ConditionMatcher, DependencyResolver> methodDependencyResolver = BindsMethodDependencyResolver.create(customizer);
            this.add(methodDependencyResolver);
            return this;
        }

        public Configurer conditionMatcher(ConditionMatcher conditionMatcher) {
            return this.conditionMatcher(ContextualInitializer.of(conditionMatcher));
        }

        public Configurer conditionMatcher(ContextualInitializer<ApplicationContext, ConditionMatcher> conditionMatcher) {
            this.conditionMatcher = conditionMatcher;
            return this;
        }
    }
}
