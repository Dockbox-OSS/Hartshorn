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

package org.dockbox.hartshorn.component.populate.context;

import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.populate.AbstractComponentPopulationStrategy;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPoint;
import org.dockbox.hartshorn.component.populate.ComponentPopulationStrategy;
import org.dockbox.hartshorn.component.populate.PopulateComponentContext;
import org.dockbox.hartshorn.component.populate.inject.AnnotatedInjectionPointRequireRule;
import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.component.populate.inject.RequireInjectionPointRule;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A {@link ComponentPopulationStrategy} which populates components with {@link org.dockbox.hartshorn.context.Context} objects.
 *
 * <p>Components can be populated with {@link org.dockbox.hartshorn.context.Context} objects by annotating the injection point with
 * {@link Context}. The {@link Context} annotation can be used to specify the name of the context to inject. If no name is specified,
 * the {@link org.dockbox.hartshorn.context.Context} with the same type as the injection point will be injected.
 *
 * <p>If multiple {@link org.dockbox.hartshorn.context.Context} objects with the same type are available, the first encountered
 * {@link org.dockbox.hartshorn.context.Context} will be injected.
 *
 * <p>Example:
 * <pre>{@code
 * @Component
 * public class MyComponent {
 *
 *   @Context("my-context")
 *   private Context myContext;
 * }
 * }</pre>
 *
 * @see org.dockbox.hartshorn.context.Context The Context interface
 * @see Context The Context annotation
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class ContextPopulationStrategy extends AbstractComponentPopulationStrategy {

    private static final Class<Context> CONTEXT_ANNOTATION = Context.class;
    private static final Class<org.dockbox.hartshorn.context.Context> CONTEXT_TYPE = org.dockbox.hartshorn.context.Context.class;

    public ContextPopulationStrategy(ApplicationContext applicationContext, Set<RequireInjectionPointRule> requiresComponentRules) {
        super(applicationContext, requiresComponentRules);
    }

    public static ContextualInitializer<ApplicationContext, ComponentPopulationStrategy> create(Customizer<Configurer> objectCustomizer) {
        return context -> {
            Configurer configurer = new Configurer();
            objectCustomizer.configure(configurer);
            return new ContextPopulationStrategy(context.input(), Set.copyOf(configurer.requiresComponentRules.initialize(context)));
        };
    }

    @Override
    protected boolean isApplicable(ComponentInjectionPoint<?> injectionPoint) {
        return injectionPoint.annotations().has(CONTEXT_ANNOTATION);
    }

    @Override
    protected Object resolveInjectedObject(InjectionPoint injectionPoint, PopulateComponentContext<?> context) {
        if (!injectionPoint.type().isChildOf(CONTEXT_TYPE)) {
            throw new IllegalStateException("Injection point " + injectionPoint.injectionPoint().qualifiedName() + " is annotated with @Context but is not a Context");
        }

        Context contextAnnotation = injectionPoint.injectionPoint().annotations().get(CONTEXT_ANNOTATION).get();
        ContextKey<? extends org.dockbox.hartshorn.context.Context> contextKey = ContextKey.builder((TypeView<? extends org.dockbox.hartshorn.context.Context>) injectionPoint.type())
                .name(contextAnnotation.value())
                .build();

        return this.applicationContext().first(contextKey).orNull();
    }

    public static class Configurer {

        private final LazyStreamableConfigurer<ApplicationContext, RequireInjectionPointRule> requiresComponentRules = LazyStreamableConfigurer.of(new AnnotatedInjectionPointRequireRule());

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
