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

package org.dockbox.hartshorn.config.jackson;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.jackson.mapping.JavaPropsDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.JsonDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.TomlDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.XmlDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.YamlDataMapper;
import org.dockbox.hartshorn.util.introspect.Introspector;

@Service
@RequiresActivator(UseConfigurations.class)
@RequiresClass("com.fasterxml.jackson.databind.ObjectMapper")
public class JacksonProviders {

    private static final int DATA_MAPPER_PHASE = ProcessingOrder.EARLY - 64;

    @Binds(value = "properties", phase = DATA_MAPPER_PHASE)
    @RequiresClass("com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper")
    public JacksonDataMapper properties() {
        return new JavaPropsDataMapper();
    }

    @Binds(value = "json", phase = DATA_MAPPER_PHASE)
    @RequiresClass("com.fasterxml.jackson.databind.json.JsonMapper")
    public JacksonDataMapper json() {
        return new JsonDataMapper();
    }

    @Binds(value = "toml", phase = DATA_MAPPER_PHASE)
    @RequiresClass("com.fasterxml.jackson.dataformat.toml.TomlMapper")
    public JacksonDataMapper toml() {
        return new TomlDataMapper();
    }

    @Binds(value = "xml", phase = DATA_MAPPER_PHASE)
    @RequiresClass("com.fasterxml.jackson.dataformat.xml.XmlMapper")
    public JacksonDataMapper xml() {
        return new XmlDataMapper();
    }

    @Binds(value = "yml", phase = DATA_MAPPER_PHASE)
    @RequiresClass("com.fasterxml.jackson.dataformat.yaml.YAMLMapper")
    public JacksonDataMapper yml() {
        return new YamlDataMapper();
    }

    @Binds(phase = DATA_MAPPER_PHASE + 32)
    public ObjectMapper objectMapper(final ApplicationContext applicationContext) {
        return new JacksonObjectMapper(applicationContext);
    }

    @Binds(phase = DATA_MAPPER_PHASE + 16) // Before ObjectMapper
    public JacksonObjectMapperConfigurator mapperConfigurator(final Introspector introspector) {
        return new StandardJacksonObjectMapperConfigurator(introspector);
    }
}
