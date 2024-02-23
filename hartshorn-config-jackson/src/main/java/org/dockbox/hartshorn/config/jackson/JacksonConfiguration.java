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

package org.dockbox.hartshorn.config.jackson;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.config.annotations.UseSerialization;
import org.dockbox.hartshorn.config.jackson.mapping.JavaPropsDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.JsonDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.TomlDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.XmlDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.YamlDataMapper;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.util.introspect.Introspector;

import jakarta.inject.Singleton;

/**
 * Default bindings for Jackson data mappers, and the {@link ObjectMapper} interface.
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseSerialization.class)
@RequiresClass("com.fasterxml.jackson.databind.ObjectMapper")
public class JacksonConfiguration {

    @Binds
    public ObjectMapper objectMapper(
            ApplicationContext applicationContext,
            JacksonDataMapperRegistry dataMapperRegistry,
            JacksonObjectMapperConfigurator objectMapperConfigurator
    ) {
        return new JacksonObjectMapper(applicationContext, dataMapperRegistry, objectMapperConfigurator);
    }

    @Binds
    @Singleton
    public JacksonDataMapperRegistry dataMapperRegistry(ComponentCollection<JacksonDataMapper> dataMappers) {
        SimpleJacksonDataMapperRegistry registry = new SimpleJacksonDataMapperRegistry();
        for (JacksonDataMapper dataMapper : dataMappers) {
            registry.register(dataMapper);
        }
        return registry;
    }

    @Binds
    @Singleton
    public JacksonObjectMapperConfigurator mapperConfigurator(Introspector introspector) {
        return new StandardJacksonObjectMapperConfigurator(introspector);
    }

    @Configuration
    @RequiresClass("com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper")
    public static class JacksonPropertiesMapperConfiguration {

        @Singleton
        @Binds(type = BindingType.COLLECTION)
        public JacksonDataMapper properties() {
            return new JavaPropsDataMapper();
        }
    }

    @Configuration
    @RequiresClass("com.fasterxml.jackson.databind.json.JsonMapper")
    public static class JacksonJsonMapperConfiguration {

        @Singleton
        @Binds(type = BindingType.COLLECTION)
        public JacksonDataMapper json() {
            return new JsonDataMapper();
        }
    }

    @Configuration
    @RequiresClass("com.fasterxml.jackson.dataformat.toml.TomlMapper")
    public static class JacksonTomlMapperConfiguration {

        @Singleton
        @Binds(type = BindingType.COLLECTION)
        public JacksonDataMapper toml() {
            return new TomlDataMapper();
        }
    }

    @Configuration
    @RequiresClass("com.fasterxml.jackson.dataformat.xml.XmlMapper")
    public static class JacksonXmlMapperConfiguration {

        @Singleton
        @Binds(type = BindingType.COLLECTION)
        public JacksonDataMapper xml() {
            return new XmlDataMapper();
        }
    }

    @Configuration
    @RequiresClass("com.fasterxml.jackson.dataformat.yaml.YAMLMapper")
    public static class JacksonYamlMapperConfiguration {

        @Singleton
        @Binds(type = BindingType.COLLECTION)
        public JacksonDataMapper yml() {
            return new YamlDataMapper();
        }
    }
}
