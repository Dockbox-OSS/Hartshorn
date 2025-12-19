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

package org.dockbox.hartshorn.launchpad.properties;

import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.util.StringUtilities;

import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Abstract base class for resolving custom properties.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractCustomPropertiesResolver implements CustomPropertiesResolver {

    @Override
    public Properties resolveProperties(
        SingleElementContext<? extends ApplicationEnvironment> initializerContext
    ) {
        Properties properties = new Properties();
        String propertyString = this.resolveStringProperties(initializerContext).stream()
            .filter(StringUtilities::notEmpty)
            .collect(Collectors.joining("\n"));
        this.safeLoadProperties(properties, new StringReader(propertyString));
        return properties;
    }

    private void safeLoadProperties(Properties properties, StringReader stringReader) {
        try {
            properties.load(stringReader);
        }
        catch (Exception e) {
            // Should never happen, as the StringReader is created from a stable String
            throw new ComponentInitializationException(
                "Failed to load command line arguments as properties",
                e);
        }
    }

    /**
     * Resolves the raw string properties, optionally using the provided initializer context for
     * additional information about the application environment.
     *
     * @param initializerContext the initializer context
     *
     * @return a list of string properties
     */
    protected abstract List<String> resolveStringProperties(
        SingleElementContext<? extends ApplicationEnvironment> initializerContext
    );
}
