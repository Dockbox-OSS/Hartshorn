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

import org.dockbox.hartshorn.application.UseBootstrap;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.contextual.StaticComponentContext;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.slf4j.Logger;

import jakarta.inject.Singleton;

/**
 * The {@link ApplicationProviders} class is responsible for providing the default {@link Logger}
 * for the application.
 *
 * @author Guus Lieben
 * @since 21.7
 */
@Service
@RequiresActivator(UseBootstrap.class)
@LogExclude
public class ApplicationProviders {

    @Binds
    public Logger logger(final ApplicationContext context) {
        return context.log();
    }

    @Binds
    @Singleton
    public ConversionService conversionService(final Introspector introspector, @Context final StaticComponentContext staticComponentContext) {
        final StandardConversionService service = new StandardConversionService(introspector).withDefaults();
        staticComponentContext.provider().all(GenericConverter.class).forEach(service::addConverter);
        staticComponentContext.provider().all(ConverterFactory.class).forEach(service::addConverterFactory);
        staticComponentContext.provider().all(Converter.class).forEach(service::addConverter);
        return service;
    }
}
