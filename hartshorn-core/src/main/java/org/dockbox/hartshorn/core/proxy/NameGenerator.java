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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.TypeContext;

/**
 * The name generator is responsible for generating the name of the proxy class. The name is generated
 * based on the type context or the type name. The way the name is generated is based on the implementation
 * of the {@link NameGenerator} interface.
 *
 * @author Guus Lieben
 * @since 22.2
 */
public interface NameGenerator {
    /**
     * Gets the name of the proxy class based on the type context.
     * @param type The type context.
     * @return The name of the proxy class.
     */
    String get(TypeContext<?> type);

    /**
     * Gets the name of the proxy class based on the type class.
     * @param type The type class.
     * @return The name of the proxy class.
     */
    String get(Class<?> type);

    /**
     * Gets the name of the proxy class based on the type name.
     * @param type The type name.
     * @return The name of the proxy class.
     */
    String get(String type);
}
