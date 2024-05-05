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

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a reference to an instance of a class. This could represent e.g. an instance of a
 * script-defined class, or a class loaded from a Java class file. Exact semantics are defined by
 * the implementation.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface InstanceReference extends PropertyContainer {

    /**
     * Returns the type of the instance. This is the type of the class that the instance is an
     * instance of. This may be a real or a synthetic type.
     *
     * @return The type of the instance.
     */
    @NonNull
    ClassReference type();
}
