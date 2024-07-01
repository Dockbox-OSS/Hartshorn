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
import org.dockbox.hartshorn.inject.annotations.Prototype;
import org.dockbox.hartshorn.inject.annotations.Singleton;
import org.dockbox.hartshorn.inject.annotations.InfrastructurePriority;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;

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

    /**
     * Prototype configuration for a classpath-based {@link ResourceLookupStrategy}. This strategy is used to
     * look up resources on the classpath.
     *
     * @return a {@link ClassPathResourceLookupStrategy}.
     */
    @Prototype
    @CompositeMember
    @InfrastructurePriority
    public ResourceLookupStrategy classPathResourceLookupStrategy() {
        return new ClassPathResourceLookupStrategy();
    }

    /**
     * Prototype configuration for a file system-based {@link ResourceLookupStrategy}. This strategy is used to
     * look up resources on the file system.
     *
     * @return a {@link FileSystemLookupStrategy}.
     */
    @Prototype
    @CompositeMember
    @InfrastructurePriority
    public ResourceLookupStrategy fileSystemResourceLookupStrategy() {
        return new FileSystemLookupStrategy();
    }

    /**
     * Singleton configuration for a strategy-based {@link ResourceLookup}. This lookup is used to find resources
     * based on a set of strategies. Strategies may be defined as composite members of {@link ResourceLookupStrategy}.
     *
     * <p>By default, this lookup will use a {@link FileSystemLookupStrategy} as a fallback strategy if no other
     * strategy can find the resource.
     *
     * @param applicationContext the application context
     * @param strategies the strategies to use
     * @return a {@link ResourceLookup} implementation
     */
    @Singleton
    @InfrastructurePriority
    public ResourceLookup resourceLookup(ApplicationContext applicationContext, ComponentCollection<ResourceLookupStrategy> strategies) {
        FallbackResourceLookup resourceLookup = new FallbackResourceLookup(applicationContext, new FileSystemLookupStrategy());
        strategies.forEach(resourceLookup::addLookupStrategy);
        return resourceLookup;
    }
}
