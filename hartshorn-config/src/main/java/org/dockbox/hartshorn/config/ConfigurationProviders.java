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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.application.ApplicationPropertyHolder;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.properties.*;

@Service
@RequiresActivator(UseConfigurations.class)
@Configuration({"application", "classpath:application"})
public class ConfigurationProviders {

    /**
     * Registers the default implementation of the property holder before any other (standard)
     * provider except the {@link ObjectMapper}, to allow it to be used directly in the
     * {@link ConfigurationObjectPostProcessor} for other provided components. The {@link ObjectMapper}
     * should be bound first, as this is typically used internally be the property holder.
     *
     * @return {@link StandardPropertyHolder}
     */
    @Binds(phase = ProcessingPriority.HIGH_PRECEDENCE)
    public PropertyHolder propertyHolder(final ApplicationContext applicationContext,
                                         final ApplicationPropertyHolder propertyHolder,
                                         final ObjectMapper objectMapper,
                                         final ObjectMapper propertyMapper) {
        return new StandardPropertyHolder(applicationContext, propertyHolder, objectMapper, propertyMapper);
    }

    @Binds(phase = ProcessingPriority.HIGH_PRECEDENCE)
    public URIConfigProcessor uriConfigProcessor() {
        return new StandardURIConfigProcessor();
    }
}
