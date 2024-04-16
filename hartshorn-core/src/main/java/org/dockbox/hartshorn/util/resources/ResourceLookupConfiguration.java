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

package org.dockbox.hartshorn.util.resources;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.processing.CompositeMember;
import org.dockbox.hartshorn.component.processing.Prototype;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;

/**
 * Configuration for {@link ResourceLookupStrategy resource lookup strategies} based {@link ResourceLookup}
 * implementations.
 *
 * @see ResourceLookup
 * @see ResourceLookupStrategy
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@Configuration
public class ResourceLookupConfiguration {

    @Prototype
    @CompositeMember
    public ResourceLookupStrategy classPathResourceLookupStrategy() {
        return new ClassPathResourceLookupStrategy();
    }

    @Prototype
    @CompositeMember
    public ResourceLookupStrategy fileSystemResourceLookupStrategy() {
        return new FileSystemLookupStrategy();
    }

    @Singleton
    public ResourceLookup resourceLookup(ApplicationContext applicationContext, ComponentCollection<ResourceLookupStrategy> strategies) {
        FallbackResourceLookup resourceLookup = new FallbackResourceLookup(applicationContext, new FileSystemLookupStrategy());
        strategies.forEach(resourceLookup::addLookupStrategy);
        return resourceLookup;
    }
}
