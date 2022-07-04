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

package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.data.annotations.Configuration;
import org.dockbox.hartshorn.data.annotations.UseConfigurations;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.config.StandardPropertyHolder;
import org.dockbox.hartshorn.data.config.StandardURIConfigProcessor;
import org.dockbox.hartshorn.data.config.URIConfigProcessor;
import org.dockbox.hartshorn.data.service.ConfigurationObjectPostProcessor;

@Service
@RequiresActivator(UseConfigurations.class)
@Configuration({"application", "classpath:application"})
public class ConfigurationProviders {

    /**
     * Registers the default implementation of the property holder before any other (standard)
     * provider except the {@link org.dockbox.hartshorn.data.mapping.ObjectMapper}, to allow
     * it to be used directly in the {@link ConfigurationObjectPostProcessor} for other provided
     * components. The {@link org.dockbox.hartshorn.data.mapping.ObjectMapper} should be bound
     * first, as this is typically used internally be the property holder.
     *
     * @return {@link StandardPropertyHolder}
     */
    @Provider(phase = ProcessingOrder.EARLY)
    public Class<? extends PropertyHolder> propertyHolder() {
        return StandardPropertyHolder.class;
    }

    @Provider(phase = ProcessingOrder.EARLY)
    public URIConfigProcessor uriConfigProcessor() {
        return new StandardURIConfigProcessor();
    }
}
