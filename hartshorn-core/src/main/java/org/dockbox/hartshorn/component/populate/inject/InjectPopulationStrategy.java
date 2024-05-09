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

package org.dockbox.hartshorn.component.populate.inject;

import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentResolutionException;
import org.dockbox.hartshorn.component.populate.AbstractComponentPopulationStrategy;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPoint;
import org.dockbox.hartshorn.component.populate.ComponentPopulationStrategy;
import org.dockbox.hartshorn.component.populate.PopulateComponentContext;
import org.dockbox.hartshorn.inject.ComponentKeyResolver;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.Inject;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Lazy;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;

/**
 * A {@link ComponentPopulationStrategy} which populates components with other components. This provides basic support for
 * {@link Inject} annotated fields, or any other annotation which is configured to be used for injection.
 *
 * <p>Injected components will be resolved by their {@link ComponentKey}, which is determined by the provided {@link
 * ComponentKeyResolver}.
 *
 * <p>By default, all components are resolved through the {@link ApplicationContext}. Additional {@link InjectParameterResolver}
 * implementations can be registered to provide custom resolution logic for specific injection points. This is primarily useful
 * for injecting components which are not registered in the {@link ApplicationContext}, or cannot be resolved through the
 * {@link ApplicationContext} alone. Built-in support for {@link org.dockbox.hartshorn.context.ContextView} types is provided by the
 * {@link InjectContextParameterResolver}.
 *
 * <p>Example:
 * <pre>{@code
 * @Component
 * public class MyComponent {
 *
 *    @Inject
 *    private MyOtherComponent otherComponent;
 *
 *    @Inject
 *    public void doSomething(@Context SampleContext context) { ... }
 * }
 * }</pre>
 *
 * @see Inject
 * @see ComponentKey
 * @see InjectParameterResolver
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class InjectPopulationStrategy extends AbstractComponentPopulationStrategy {

    private final Lazy<ConversionService> conversionService;
    private final Set<InjectParameterResolver> parameterResolvers;

    protected InjectPopulationStrategy(
            ApplicationContext applicationContext,
            Set<RequireInjectionPointRule> requiresComponentRules,
            Set<InjectParameterResolver> parameterResolvers) {
        super(applicationContext, requiresComponentRules);
        this.parameterResolvers = parameterResolvers;
        this.conversionService = Lazy.of(applicationContext, ConversionService.class);
    }

    @Override
    protected boolean isApplicable(ComponentInjectionPoint<?> injectionPoint) {
        return this.applicationContext()
                .environment()
                .injectionPointsResolver()
                .isInjectable(injectionPoint.declaration());
    }

    @Override
    protected Object resolveInjectedObject(InjectionPoint injectionPoint, PopulateComponentContext<?> context) throws ComponentResolutionException {
        for(InjectParameterResolver resolver : this.parameterResolvers) {
            if (resolver.accepts(injectionPoint)) {
                Object resolved = resolver.resolve(injectionPoint, context);
                // Parameter resolvers are expected to provide compatible instances, or null if they cannot resolve the injection point.
                // If a non-null value is provided, it must be compatible with the injection point type. If it is not, we do not want
                // to attempt a manual conversion through the ConversionService, as we cannot make assumptions about custom implementations,
                // and thus risk resulting in unexpected behaviour.
                if (resolved == null || injectionPoint.type().isInstance(resolved)) {
                    return resolved;
                }
                else {
                    throw new ComponentResolutionException("Failed to resolve injection point " + injectionPoint.injectionPoint().qualifiedName() + ", expected type " + injectionPoint.type().type().getName() + " but got " + resolved.getClass().getName(), null);
                }
            }
        }

        ComponentKey<?> componentKey = this.applicationContext().environment().componentKeyResolver().resolve(injectionPoint.injectionPoint());
        ComponentRequestContext requestContext = ComponentRequestContext.createForInjectionPoint(injectionPoint);
        Object component = this.applicationContext().get(componentKey, requestContext);

        // Ensure types are compatible, or a default value is provided if it is available. This primarily
        // applies to component collections.
        return this.conversionService.get().convert(component, injectionPoint.type().type());
    }

    public static ContextualInitializer<ApplicationContext, ComponentPopulationStrategy> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new InjectPopulationStrategy(
                    context.input(),
                    Set.copyOf(configurer.requiresComponentRules.initialize(context)),
                    Set.copyOf(configurer.parameterResolvers.initialize(context))
            );
        };
    }

    /**
     * TODO: #1060 Add documentation
     *
     * @since 0.6.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private final LazyStreamableConfigurer<ApplicationContext, RequireInjectionPointRule> requiresComponentRules = LazyStreamableConfigurer.of(new AnnotatedInjectionPointRequireRule());
        private final LazyStreamableConfigurer<ApplicationContext, InjectParameterResolver> parameterResolvers = LazyStreamableConfigurer.ofInitializer(context -> new InjectContextParameterResolver(context.input()));

        public Configurer requiresComponentRules(RequireInjectionPointRule... requiresComponentRules) {
            this.requiresComponentRules.customizer(collection -> collection.addAll(requiresComponentRules));
            return this;
        }

        public Configurer requiresComponentRules(Set<RequireInjectionPointRule> requiresComponentRules) {
            this.requiresComponentRules.customizer(collection -> collection.addAll(requiresComponentRules));
            return this;
        }

        public Configurer requiresComponentRules(Customizer<StreamableConfigurer<ApplicationContext, RequireInjectionPointRule>> customizer) {
            this.requiresComponentRules.customizer(customizer);
            return this;
        }

        public Configurer parameterResolvers(InjectParameterResolver... parameterResolvers) {
            this.parameterResolvers.customizer(collection -> collection.addAll(parameterResolvers));
            return this;
        }

        public Configurer parameterResolvers(Set<InjectParameterResolver> parameterResolvers) {
            this.parameterResolvers.customizer(collection -> collection.addAll(parameterResolvers));
            return this;
        }

        public Configurer parameterResolvers(Customizer<StreamableConfigurer<ApplicationContext, InjectParameterResolver>> customizer) {
            this.parameterResolvers.customizer(customizer);
            return this;
        }

    }
}
