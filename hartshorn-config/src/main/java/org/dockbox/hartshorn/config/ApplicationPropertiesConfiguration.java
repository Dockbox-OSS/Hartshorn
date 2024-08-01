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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.application.ApplicationPropertyHolder;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.config.annotations.IncludeResourceConfiguration;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.dockbox.hartshorn.config.properties.StandardPropertyHolder;
import org.dockbox.hartshorn.config.properties.StandardURIConfigProcessor;
import org.dockbox.hartshorn.config.properties.URIConfigProcessor;
import org.dockbox.hartshorn.inject.InfrastructurePriority;

/**
 * TODO: #1062 Add documentation
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseConfigurations.class)
@IncludeResourceConfiguration({"fs:application", "classpath:application"})
public class ApplicationPropertiesConfiguration {

    @Singleton
    @InfrastructurePriority
    public PropertyHolder propertyHolder(ApplicationPropertyHolder propertyHolder,
                                         ObjectMapper objectMapper,
                                         ObjectMapper propertyMapper) throws ObjectMappingException {
        return new StandardPropertyHolder(propertyHolder, objectMapper, propertyMapper);
    }

    @Singleton
    @InfrastructurePriority
    public URIConfigProcessor uriConfigProcessor() {
        return new StandardURIConfigProcessor();
    }
}
