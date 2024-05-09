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

package test.org.dockbox.hartshorn.config.jackson;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.config.jackson.JacksonDataMapperRegistry;
import org.dockbox.hartshorn.config.jackson.JacksonObjectMapper;
import org.dockbox.hartshorn.config.jackson.JacksonObjectMapperConfigurator;

import org.dockbox.hartshorn.inject.Inject;
import test.org.dockbox.hartshorn.config.mapping.PersistenceModifiersTests;

public class JacksonPersistenceModifiersTests extends PersistenceModifiersTests {

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private JacksonDataMapperRegistry dataMapperRegistry;

    @Inject
    private JacksonObjectMapperConfigurator objectMapperConfigurator;

    @Override
    protected ObjectMapper objectMapper() {
        return new JacksonObjectMapper(this.applicationContext, this.dataMapperRegistry, this.objectMapperConfigurator);
    }
}
