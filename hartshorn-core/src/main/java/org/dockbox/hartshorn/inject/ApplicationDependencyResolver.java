package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.strategy.BindingStrategyRegistry;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyInitializer;
import org.dockbox.hartshorn.util.StreamableConfigurer;

import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationDependencyResolver extends CompositeDependencyResolver {

    public ApplicationDependencyResolver(final Set<DependencyResolver> resolvers) {
        super(resolvers);
    }

    public static LazyInitializer<ConditionMatcher, DependencyResolver> create(final Customizer<Configurer> customizer) {
        return conditionMatcher -> {
            final Configurer configurer = new Configurer()
                    .withManagedComponents()
                    .withBindsMethods(Customizer.useDefaults());

            customizer.configure(configurer);
            return new CompositeDependencyResolver(configurer.stream()
                    .map(initializer -> initializer.initialize(conditionMatcher))
                    .collect(Collectors.toSet()));
        };
    }

    public static class Configurer extends StreamableConfigurer<ConditionMatcher, DependencyResolver> {

        public Configurer withManagedComponents() {
            this.add(new ComponentDependencyResolver());
            return this;
        }

        public Configurer withBindsMethods(final Customizer<BindingStrategyRegistry> customizer) {
            final LazyInitializer<ConditionMatcher, DependencyResolver> methodDependencyResolver = BindsMethodDependencyResolver.create(customizer);
            this.add(methodDependencyResolver);
            return this;
        }

    }
}
