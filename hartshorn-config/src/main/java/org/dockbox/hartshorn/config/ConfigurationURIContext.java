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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.component.ComponentKey;

import java.net.URI;

/**
 * TODO: #1062 Add documentation
 *
 * @param <?>> ...
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public record ConfigurationURIContext(URI uri, ComponentKey<?> key, String source) {
}
