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

import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;

// Only require Jackson Core to be present, individual providers validate the presence of
// required mapper implementations.
@Service(activators = UsePersistence.class, requires = "com.fasterxml.jackson.databind.ObjectMapper")
public class JacksonProviders {

    @Provider("properties")
    public JacksonDataMapper properties() {
        if (TypeContext.lookup("com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper").isVoid())
            throw new IllegalStateException("Java properties is not available");
        return new JavaPropsDataMapper();
    }

    @Provider("json")
    public JacksonDataMapper json() {
        if (TypeContext.lookup("com.fasterxml.jackson.databind.json.JsonMapper").isVoid())
            throw new IllegalStateException("Jackson is not available");
        return new JsonDataMapper();
    }

    @Provider("toml")
    public JacksonDataMapper toml() {
        if (TypeContext.lookup("com.fasterxml.jackson.dataformat.toml.TomlMapper").isVoid())
            throw new IllegalStateException("TOML is not available");
        return new TomlDataMapper();
    }

    @Provider("xml")
    public JacksonDataMapper xml() {
        if (TypeContext.lookup("com.fasterxml.jackson.dataformat.xml.XmlMapper").isVoid())
            throw new IllegalStateException("XML is not available");
        return new XmlDataMapper();
    }

    @Provider("yml")
    public JacksonDataMapper yml() {
        if (TypeContext.lookup("com.fasterxml.jackson.dataformat.yaml.YAMLMapper").isVoid())
            throw new IllegalStateException("YAML is not available");
        return new YamlDataMapper();
    }

    @Provider
    public ObjectMapper objectMapper() {
        return new JacksonObjectMapper();
    }
}
