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

import java.util.function.Supplier;
import org.dockbox.hartshorn.inject.ComponentKey;
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
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.ApplicationStarter;
import org.dockbox.hartshorn.launchpad.annotations.LoggerMeta;
import org.dockbox.hartshorn.launchpad.annotations.UseLaunchpad;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.properties.convert.ValuePropertyToObjectConverterFactory;
import org.dockbox.hartshorn.util.ApplicationException;
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

    /**
     * Provides a logger that uses the component name as logger name, if available. If not, falls
     * back to the declaring type's name if the injection point is known, or the current thread's
     * name otherwise.
     *
     * @param injectionPoint the injection point, if available
     * @param componentRegistry the component registry, to resolve component names
     * @param declarationResolver the injection point declaration resolver
     *
     * @return the logger
     */
    @Prototype
    @InfrastructurePriority
    @RequiresProperty(name = "hartshorn.logging.naming.use-container-names", withValue = "true")
    public Logger logger(
        @Required(false) InjectionPoint injectionPoint,
        ComponentRegistry componentRegistry,
        InjectionPointDeclarationResolver declarationResolver
    ) {
        if (injectionPoint == null) {
            return this.defaultLogger();
        }
        return this.loggerForInjectionPoint(injectionPoint, () -> {
            Class<?> declaringType = declarationResolver.resolve(injectionPoint).type();
            return componentRegistry.container(declaringType)
                    .map(ComponentContainer::name)
                    .orElseGet(declaringType::getName);
        });
    }

    /**
     * Provides a logger that uses the declaring type's name as logger name, or the current thread's
     * name if the injection point is not known.
     *
     * @param injectionPoint the injection point, if available
     * @param declarationResolver the injection point declaration resolver
     * @return the logger
     */
    @Prototype
    @InfrastructurePriority
    @RequiresAbsentBinding(Logger.class)
    public Logger logger(
        @Required(false) InjectionPoint injectionPoint,
        InjectionPointDeclarationResolver declarationResolver
    ) {
        if (injectionPoint == null) {
            return this.defaultLogger();
        }
        return this.loggerForInjectionPoint(injectionPoint, () -> {
            Class<?> declaringType = declarationResolver.resolve(injectionPoint).type();
            return declaringType.getName();
        });
    }

    private Logger loggerForInjectionPoint(
            InjectionPoint injectionPoint,
            Supplier<String> defaultNameSupplier
    ) {
        String name = injectionPoint.injectionPoint().annotations()
                .get(LoggerMeta.class)
                .map(LoggerMeta::name)
                .filter(StringUtilities::notEmpty)
                .orElseGet(defaultNameSupplier);
        return LoggerFactory.getLogger(name);
    }

    private Logger defaultLogger() {
        Thread currentThread = Thread.currentThread();
        return LoggerFactory.getLogger(currentThread.getName());
    }

    /**
     * Provides the standard {@link SimpleInjectionPointDeclarationResolver} implementation of
     * {@link InjectionPointDeclarationResolver}.
     *
     * @return the injection point declaration resolver
     */
    @Singleton
    @InfrastructurePriority
    public InjectionPointDeclarationResolver injectionPointDeclarationResolver() {
        return new SimpleInjectionPointDeclarationResolver();
    }

    /**
     * Provides the standard {@link ConversionService} implementation, applying all default- and
     * discovered converters.
     *
     * @param introspector the type introspector to use
     * @param genericConverters discovered generic converters
     * @param converterFactories discovered converter factories
     * @param converters discovered converters
     * @param customizers discovered customizers
     *
     * @return the conversion service
     */
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

    /**
     * Provides additional internal converters for the conversion service. Currently, this
     * only includes {@link ValuePropertyToObjectConverterFactory} to convert from
     * {@link ValueProperty} instances.
     *
     * @return the converters customizer
     */
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

    /**
     * Provides a lifecycle observer that runs the {@link ApplicationStarter} once the application
     * has started. The observer performs a late lookup for the {@link ApplicationStarter} to allow
     * for maximum flexibility early in the application lifecycle.
     *
     * @return the lifecycle observer
     */
    @Singleton
    @CompositeMember
    public LifecycleObserver applicationStarterLifecycleObserver() {
        return new LifecycleObserver() {
            @Override
            public void onStarted(ApplicationContext applicationContext) {
                // Late lookup for ApplicationStarter, to allow for maximum flexibility early in the
                // application lifecycle.
                ComponentKey<ApplicationStarter> componentKey = ComponentKey
                    .builder(ApplicationStarter.class)
                    .strict(false)
                    .optional()
                    .build();
                ApplicationStarter applicationStarter = applicationContext.get(componentKey);
                // OK to do nothing if no ApplicationStarter is present, as this is optional. Other
                // observers may still be present, and will be invoked.
                if (applicationStarter != null) {
                    try {
                        applicationStarter.run(applicationContext);
                    }
                    catch (ApplicationException e) {
                        applicationContext.handle(e);
                    }
                }
            }
        };
    }
}
