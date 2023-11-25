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

package org.dockbox.hartshorn.component.populate.inject;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentResolutionException;
import org.dockbox.hartshorn.component.populate.AbstractComponentPopulationStrategy;
import org.dockbox.hartshorn.component.populate.ComponentPopulationStrategy;
import org.dockbox.hartshorn.component.populate.PopulateComponentContext;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Lazy;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;

import jakarta.inject.Inject;

public class InjectPopulationStrategy extends AbstractComponentPopulationStrategy {

    private final Set<Class<? extends Annotation>> injectAnnotations;
    private final ComponentKeyResolver componentKeyResolver;
    private final Lazy<ConversionService> conversionService;
    private final Set<InjectParameterResolver> parameterResolvers;

    protected InjectPopulationStrategy(
            ApplicationContext applicationContext,
            Set<Class<? extends Annotation>> injectAnnotations,
            Set<InjectionPointNameResolver> nameResolvers,
            Set<EnableInjectionPointRule> enableComponentRules,
            Set<RequireInjectionPointRule> requiresComponentRules,
            Set<InjectParameterResolver> parameterResolvers) {
        super(applicationContext, requiresComponentRules);
        this.injectAnnotations = injectAnnotations;
        this.parameterResolvers = parameterResolvers;
        this.componentKeyResolver = new ComponentKeyResolver(
                nameResolvers,
                enableComponentRules
        );
        this.conversionService = Lazy.of(applicationContext, ConversionService.class);
    }

    @Override
    protected boolean isApplicable(AnnotatedElementView injectionPoint) {
        return injectionPoint.annotations().hasAny(this.injectAnnotations);
    }

    @Override
    protected Object resolveInjectedObject(InjectionPoint injectionPoint, PopulateComponentContext<?> context) throws ComponentResolutionException {
        for(InjectParameterResolver resolver : parameterResolvers) {
            if (resolver.accepts(injectionPoint)) {
                return resolver.resolve(injectionPoint, context);
            }
        }

        ComponentKey<?> componentKey = componentKeyResolver.buildComponentKey(injectionPoint);
        Object component = this.applicationContext().get(componentKey);
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
                    Set.copyOf(configurer.annotations.initialize(context)),
                    Set.copyOf(configurer.nameResolvers.initialize(context)),
                    Set.copyOf(configurer.enableComponentRules.initialize(context)),
                    Set.copyOf(configurer.requiresComponentRules.initialize(context)),
                    Set.copyOf(configurer.parameterResolvers.initialize(context))
            );
        };
    }

    public static class Configurer {

        private final LazyStreamableConfigurer<ApplicationContext, Class<? extends Annotation>> annotations = LazyStreamableConfigurer.of(Inject.class);
        private final LazyStreamableConfigurer<ApplicationContext, InjectionPointNameResolver> nameResolvers = LazyStreamableConfigurer.of(new AnnotatedInjectionPointNameResolver());
        private final LazyStreamableConfigurer<ApplicationContext, EnableInjectionPointRule> enableComponentRules = LazyStreamableConfigurer.of(new AnnotatedInjectionPointEnableRule());
        private final LazyStreamableConfigurer<ApplicationContext, RequireInjectionPointRule> requiresComponentRules = LazyStreamableConfigurer.of(new AnnotatedInjectionPointRequireRule());
        private final LazyStreamableConfigurer<ApplicationContext, InjectParameterResolver> parameterResolvers = LazyStreamableConfigurer.of(new InjectContextParameterResolver());

        @SafeVarargs
        public final Configurer annotations(Class<? extends Annotation>... annotations) {
            this.annotations.customizer(collection -> collection.addAll(annotations));
            return this;
        }

        public Configurer annotations(Set<Class<? extends Annotation>> annotations) {
            this.annotations.customizer(collection -> collection.addAll(annotations));
            return this;
        }

        public Configurer annotations(Customizer<StreamableConfigurer<ApplicationContext, Class<? extends Annotation>>> customizer) {
            this.annotations.customizer(customizer);
            return this;
        }

        public Configurer nameResolvers(InjectionPointNameResolver... nameResolvers) {
            this.nameResolvers.customizer(collection -> collection.addAll(nameResolvers));
            return this;
        }

        public Configurer nameResolvers(Set<InjectionPointNameResolver> nameResolvers) {
            this.nameResolvers.customizer(collection -> collection.addAll(nameResolvers));
            return this;
        }

        public Configurer nameResolvers(Customizer<StreamableConfigurer<ApplicationContext, InjectionPointNameResolver>> customizer) {
            this.nameResolvers.customizer(customizer);
            return this;
        }

        public Configurer enableComponentRules(EnableInjectionPointRule... enableComponentRules) {
            this.enableComponentRules.customizer(collection -> collection.addAll(enableComponentRules));
            return this;
        }

        public Configurer enableComponentRules(Set<EnableInjectionPointRule> enableComponentRules) {
            this.enableComponentRules.customizer(collection -> collection.addAll(enableComponentRules));
            return this;
        }

        public Configurer enableComponentRules(Customizer<StreamableConfigurer<ApplicationContext, EnableInjectionPointRule>> customizer) {
            this.enableComponentRules.customizer(customizer);
            return this;
        }

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
    }
}
