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

package org.dockbox.hartshorn.component;

import jakarta.inject.Singleton;
import org.dockbox.hartshorn.application.UseBootstrap;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.Strict;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.slf4j.Logger;

/**
 * Configuration for core components that are required by the framework. This includes the {@link Logger} and
 * {@link ConversionService}.
 *
 * @author Guus Lieben
 * @since 0.4.6
 */
@Service
@RequiresActivator(UseBootstrap.class)
@LogExclude
public class ApplicationProviders {

    @Binds
    public Logger logger(ApplicationContext context) {
        return context.log();
    }

    @Binds
    @Singleton
    public ConversionService conversionService(
            Introspector introspector,
            @Strict(false) ComponentCollection<GenericConverter> genericConverters,
            @Strict(false) ComponentCollection<ConverterFactory<?, ?>> converterFactories,
            @Strict(false) ComponentCollection<Converter<?, ?>> converters
    ) {
        StandardConversionService service = new StandardConversionService(introspector).withDefaults();
        
        genericConverters.forEach(service::addConverter);
        converterFactories.forEach(service::addConverterFactory);
        converters.forEach(service::addConverter);

        return service;
    }
}
