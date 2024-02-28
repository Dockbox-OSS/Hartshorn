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

package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.util.resources.ClassPathResourceLookupStrategy;
import org.dockbox.hartshorn.util.resources.ClasspathResourceLocator;
import org.dockbox.hartshorn.util.resources.FallbackResourceLookup;
import org.dockbox.hartshorn.util.resources.FileSystemLookupStrategy;
import org.dockbox.hartshorn.util.resources.FileSystemProvider;
import org.dockbox.hartshorn.util.resources.ResourceLookup;
import org.dockbox.hartshorn.util.resources.ResourceLookupStrategy;

import jakarta.inject.Singleton;

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

    @Binds
    @Singleton
    public ClasspathResourceLocator classpathResourceLocator(ApplicationEnvironment environment) {
        return environment.classpath();
    }

    @Binds
    @Singleton
    public FileSystemProvider fileSystemProvider(ApplicationEnvironment environment) {
        return environment.fileSystem();
    }

    @Singleton
    @Binds(type = BindingType.COLLECTION)
    public ResourceLookupStrategy classPathResourceLookupStrategy(ClasspathResourceLocator resourceLocator) {
        return new ClassPathResourceLookupStrategy(resourceLocator);
    }

    @Singleton
    @Binds(type = BindingType.COLLECTION)
    public ResourceLookupStrategy fileSystemResourceLookupStrategy(FileSystemProvider fileSystemProvider) {
        return new FileSystemLookupStrategy(fileSystemProvider);
    }

    @Binds
    @Singleton
    public ResourceLookup resourceLookup(FileSystemProvider fileSystemProvider, ComponentCollection<ResourceLookupStrategy> strategies) {
        FallbackResourceLookup resourceLookup = new FallbackResourceLookup(new FileSystemLookupStrategy(fileSystemProvider));
        strategies.forEach(resourceLookup::addLookupStrategy);
        return resourceLookup;
    }
}
