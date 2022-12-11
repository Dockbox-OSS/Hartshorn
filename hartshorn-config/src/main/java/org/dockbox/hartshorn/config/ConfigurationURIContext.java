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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.config.resource.ResourceLookupStrategy;

import java.net.URI;

public class ConfigurationURIContext {

    private final URI uri;
    private final ComponentKey<?> key;
    private final String source;
    private final ResourceLookupStrategy strategy;

    public ConfigurationURIContext(final URI uri, final ComponentKey<?> key, final String source, final ResourceLookupStrategy strategy) {
        this.uri = uri;
        this.key = key;
        this.source = source;
        this.strategy = strategy;
    }

    public URI uri() {
        return this.uri;
    }

    public ComponentKey<?> key() {
        return this.key;
    }

    public String source() {
        return this.source;
    }

    public ResourceLookupStrategy strategy() {
        return this.strategy;
    }
}
