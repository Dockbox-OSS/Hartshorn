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

package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.Fuzzy;
import org.dockbox.hartshorn.inject.annotations.InfrastructurePriority;
import org.dockbox.hartshorn.inject.annotations.Required;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.condition.support.RequiresAbsentBinding;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.inject.targets.InjectionPoint;
import org.dockbox.hartshorn.launchpad.annotations.LoggerMeta;
import org.dockbox.hartshorn.launchpad.annotations.UseLaunchpad;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.properties.convert.ValuePropertyToObjectConverterFactory;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.ConvertersCustomizer;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Configuration for core components that are required by the framework. This includes the
 * {@link Logger} and {@link ConversionService}.
 *
 * @since 0.4.6
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseLaunchpad.class)
public class LaunchpadSharedComponentConfiguration {

    @Prototype
    @InfrastructurePriority
    @RequiresProperty(name = "hartshorn.logging.naming.use-container-names", withValue = "true")
    public Logger logger(
            @Required(false) InjectionPoint injectionPoint,
            ComponentRegistry componentRegistry,
            InjectionPointDeclarationResolver declarationResolver
    ) {
        return logger(injectionPoint, () -> {
            Class<?> declaringType = declarationResolver.resolve(injectionPoint).type();
            return componentRegistry.container(declaringType)
                    .map(ComponentContainer::name)
                    .map(LoggerFactory::getLogger)
                    .orElseGet(() -> LoggerFactory.getLogger(declaringType));
        });
    }

    @Prototype
    @InfrastructurePriority
    @RequiresAbsentBinding(Logger.class)
    public Logger logger(
            @Required(false) InjectionPoint injectionPoint,
            InjectionPointDeclarationResolver declarationResolver
    ) {
        return logger(injectionPoint, () -> {
            Class<?> declaringType = declarationResolver.resolve(injectionPoint).type();
            return LoggerFactory.getLogger(declaringType);
        });
    }

    protected Logger logger(InjectionPoint injectionPoint, Supplier<Logger> defaultValue) {
        if (injectionPoint == null) {
            return defaultValue.get();
        }
        return injectionPoint.injectionPoint().annotations()
                .get(LoggerMeta.class)
                .map(LoggerMeta::name)
                .filter(StringUtilities::notEmpty)
                .map(LoggerFactory::getLogger)
                .orElseGet(defaultValue);
    }

    @Singleton
    @InfrastructurePriority
    public InjectionPointDeclarationResolver injectionPointDeclarationResolver() {
        return new SimpleInjectionPointDeclarationResolver();
    }

    @Singleton
    @InfrastructurePriority
    public ConversionService conversionService(
            Introspector introspector,
            @Fuzzy ComponentCollection<GenericConverter> genericConverters,
            @Fuzzy ComponentCollection<ConverterFactory<?, ?>> converterFactories,
            @Fuzzy ComponentCollection<Converter<?, ?>> converters,
            @Fuzzy ComponentCollection<ConvertersCustomizer> customizers
    ) {
        StandardConversionService service = new StandardConversionService(introspector)
                .withDefaults();

        genericConverters.forEach(service::addConverter);
        converterFactories.forEach(service::addConverterFactory);
        converters.forEach(service::addConverter);

        customizers.forEach(customizer -> customizer.configure(service, service));

        return service;
    }

    @Singleton
    @CompositeMember
    public ConvertersCustomizer additionalInternalConvertersCustomizer() {
        return (conversionService, converterRegistry) -> {
            // From Hartshorn Properties
            converterRegistry.addConverterFactory(
                    ValueProperty.class,
                    new ValuePropertyToObjectConverterFactory(conversionService)
            );
        };
    }

    @Singleton
    @CompositeMember
    public LifecycleObserver applicationStarterLifecycleObserver() {
        return new ApplicationStarterLifecycleObserver();
    }
}
