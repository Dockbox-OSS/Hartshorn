/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.properties.loader.support;

import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.loader.FilePropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.PredicatePropertyRegistryLoader;
import org.dockbox.hartshorn.util.IOUtilities;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

/**
 * An SPI wrapper for {@link JacksonYamlPropertyRegistryLoader}, to safely allow for safe runtime
 * interactions, even if required dependencies are missing.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JacksonYamlSpiPropertyRegistryLoader
        implements PredicatePropertyRegistryLoader, FilePropertyRegistryLoader {

    public static final boolean CAN_LOAD =
            TypeUtils.exists("tools.jackson.dataformat.yaml.YAMLMapper");

    @Override
    public Set<String> supportedExtensions() {
        // If Jackson YAML is absent, the loader cannot support any extension.
        if (CAN_LOAD) {
            return JacksonYamlPropertyRegistryLoader.DEFAULT_EXTENSIONS;
        }
        return Set.of();
    }

    @Override
    public boolean isCompatible(URI path) {
        return CAN_LOAD && JacksonYamlPropertyRegistryLoader.DEFAULT_EXTENSIONS.contains(
                IOUtilities.getFileExtension(path)
        );
    }

    @Override
    public void loadRegistry(PropertyRegistry registry, URI path) throws IOException {
        if (CAN_LOAD) {
            DelegateHolder.INSTANCE.loadRegistry(registry, path);
        }
        else {
            throw new IllegalStateException(
                    "Jackson YAML support is not available (missing jackson-dataformat-yaml)"
            );
        }
    }

    private static final class DelegateHolder {
        static final JacksonYamlPropertyRegistryLoader INSTANCE =
                new JacksonYamlPropertyRegistryLoader();
    }
}
