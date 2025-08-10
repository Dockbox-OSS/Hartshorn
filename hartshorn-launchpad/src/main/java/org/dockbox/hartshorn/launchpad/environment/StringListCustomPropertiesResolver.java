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

import java.util.List;
import org.dockbox.hartshorn.context.SingleElementContext;

/**
 * A properties resolver that resolves a list of properties from a given list of strings. The string
 * values should be formatted as key-value pairs, compatible with the Java Properties format.
 *
 * <p>This resolver is typically used to provide custom properties that are not sourced from
 * external files or resources, but rather defined directly in the code or passed
 * as command line arguments. It is useful for scenarios where properties need to be
 * dynamically defined or configured at runtime, such as in testing environments or
 * during application startup.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StringListCustomPropertiesResolver extends AbstractCustomPropertiesResolver {

    private final List<String> properties;

    public StringListCustomPropertiesResolver(List<String> properties) {
        this.properties = properties;
    }

    @Override
    protected List<String> resolveStringProperties(SingleElementContext<ApplicationEnvironment> initializerContext) {
        return this.properties;
    }
}
