package org.dockbox.hartshorn.inject;

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
        return applicationContext -> {
            Configurer configurer = new Configurer()
                    .withManagedComponents()
                    .withBindsMethods(Customizer.useDefaults());

            customizer.configure(configurer);

            ConditionMatcher conditionMatcher = configurer.conditionMatcher.initialize(applicationContext);
            Set<DependencyResolver> resolvers = configurer.stream()
                    .map(initializer -> initializer.initialize(conditionMatcher))
                    .collect(Collectors.toSet());

            return new CompositeDependencyResolver(resolvers);
        };
    }

    public static class Configurer extends StreamableConfigurer<ConditionMatcher, DependencyResolver> {

        private ContextualInitializer<ApplicationContext, ConditionMatcher> conditionMatcher = ConditionMatcher::new;

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
