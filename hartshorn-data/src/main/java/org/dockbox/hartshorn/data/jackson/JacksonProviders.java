/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.data.jackson;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;

@Service
@RequiresActivator(UsePersistence.class)
@RequiresClass("com.fasterxml.jackson.databind.ObjectMapper")
public class JacksonProviders {

    @Provider("properties")
    @RequiresClass("com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper")
    public JacksonDataMapper properties() {
        return new JavaPropsDataMapper();
    }

    @Provider("json")
    @RequiresClass("com.fasterxml.jackson.databind.json.JsonMapper")
    public JacksonDataMapper json() {
        return new JsonDataMapper();
    }

    @Provider("toml")
    @RequiresClass("com.fasterxml.jackson.dataformat.toml.TomlMapper")
    public JacksonDataMapper toml() {
        return new TomlDataMapper();
    }

    @Provider("xml")
    @RequiresClass("com.fasterxml.jackson.dataformat.xml.XmlMapper")
    public JacksonDataMapper xml() {
        return new XmlDataMapper();
    }

    @Provider("yml")
    @RequiresClass("com.fasterxml.jackson.dataformat.yaml.YAMLMapper")
    public JacksonDataMapper yml() {
        return new YamlDataMapper();
    }

    @Provider
    public ObjectMapper objectMapper() {
        return new JacksonObjectMapper();
    }
}
