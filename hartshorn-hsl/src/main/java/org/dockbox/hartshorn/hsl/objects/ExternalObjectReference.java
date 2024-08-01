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

package org.dockbox.hartshorn.hsl.objects;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents an object on the heap that is not a script-defined class. This could be e.g. a Java
 * class, or a primitive value. This allows for interoperability between the script and the host
 * environment.
 *
 * <p>External objects are {@link InstanceReference}s, and can be used as such. They can be bound to
 * a {@link BindableNode}, and can be passed as an argument to a {@link CallableNode}. They can also
 * be used as a {@link PropertyContainer}, though are not guaranteed to be mutable.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface ExternalObjectReference extends InstanceReference {

    /**
     * Returns the external object that this reference represents. This can be any object, including
     * {@code null}.
     *
     * @return The external object.
     */
    @Nullable
    Object externalObject();
}
