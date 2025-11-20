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

package org.dockbox.hartshorn.launchpad.environment;

import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.launchpad.properties.PropertySourceResolver;

import java.util.Properties;

/**
 * Functional interface for resolving custom properties. This interface is used to provide a
 * mechanism for resolving properties that do not reside in a specific resource or file, which would
 * otherwise be resolved through {@link PropertySourceResolver}s.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
@FunctionalInterface
public interface CustomPropertiesResolver {

    /**
     * Resolves the properties for the application. This method is called during the initialization
     * of the application environment, and should not depend on other components.
     *
     * @param initializerContext the context for the application environment initialization
     *
     * @return the resolved properties
     */
    Properties resolveProperties(SingleElementContext<ApplicationEnvironment> initializerContext);
}
