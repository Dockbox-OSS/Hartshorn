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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.SimpleApplicationContext;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentPostConstructorImpl;
import org.dockbox.hartshorn.component.ScopeAwareComponentProvider;
import org.dockbox.hartshorn.component.populate.MethodsAndFieldsInjectionPointResolver;
import org.dockbox.hartshorn.component.processing.ComponentFinalizingPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Initializer;
import org.dockbox.hartshorn.util.StreamableConfigurer;

public class HartshornApplicationStandardInitializer {

    public static Initializer<ApplicationContext> create(Customizer<HartshornApplicationConfigurer> customizer) {
        return () -> {
            HartshornApplicationConfigurer configurer = new HartshornApplicationConfigurer();
            customizer.configure(configurer);
            return StandardApplicationBuilder.create(builder -> configureApplicationBuilder(builder, configurer)).create();
        };
    }

    private static void configureApplicationBuilder(StandardApplicationBuilder.Configurer builder, HartshornApplicationConfigurer configurer) {
        configurer.applicationBuilder.configure(builder);
        builder.constructor(StandardApplicationContextConstructor.create(constructor -> configureApplicationConstructor(configurer, constructor)));
    }

    private static void configureApplicationConstructor(HartshornApplicationConfigurer configurer, StandardApplicationContextConstructor.Configurer constructor) {
        configurer.applicationContextConstructor.configure(constructor);
        constructor.componentPostProcessors(processors -> configurePostProcessors(configurer, processors));
        constructor.environment(
            ContextualApplicationEnvironment.create(environment -> configureApplicationEnvironment(configurer, environment)));
    }

    private static void configurePostProcessors(HartshornApplicationConfigurer configurer,
        StreamableConfigurer<ApplicationContext, ComponentPostProcessor> processors) {
        processors.clear();
        processors.add(ComponentFinalizingPostProcessor.create(configurer.postProcessor));
    }

    private static void configureApplicationEnvironment(HartshornApplicationConfigurer configurer, ContextualApplicationEnvironment.Configurer environment) {
        configurer.environment.configure(environment);
        environment.applicationContext(SimpleApplicationContext.create(context -> configureApplicationContext(configurer, context)));
        environment.injectionPointsResolver(MethodsAndFieldsInjectionPointResolver.create(configurer.injectionPointResolver));
    }

    private static void configureApplicationContext(HartshornApplicationConfigurer configurer, SimpleApplicationContext.Configurer context) {
        configurer.applicationContext.configure(context);
        context.componentProvider(ScopeAwareComponentProvider.create(provider -> configureComponentProvider(configurer, provider)));
    }

    private static void configureComponentProvider(HartshornApplicationConfigurer configurer, ScopeAwareComponentProvider.Configurer provider) {
        configurer.componentProvider.configure(provider);
        provider.componentPostConstructor(ComponentPostConstructorImpl.create(configurer.componentPostConstructor));
    }
}
