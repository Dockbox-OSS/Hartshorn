/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util;

/**
 * A named element is one which has a simple name. The name does not have to be unique, and
 * may or may not be formalized in any way.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface Named {

    /**
     * Returns the simple name of the element. This can represent the actual {@code name}
     * property, or a derived value, such as the name of a field or method.
     *
     * @return the simple name of the element
     */
    String name();
}
