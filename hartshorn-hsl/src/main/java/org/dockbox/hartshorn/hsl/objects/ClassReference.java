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

import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;

/**
 * Represents a reference to a class. This could represent e.g. a script-defined class, or a class
 * defined in a Java class file. Exact semantics are defined by the implementation.
 *
 * <p>Class references are {@link CallableNode}s, and can thus be instantiated. Implementations may
 * choose to provide a default constructor, or require arguments to be provided. If an implementation
 * provides a default constructor, it may choose to ignore the given arguments when constructing the
 * instance.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface ClassReference extends CallableNode, Finalizable {

    /**
     * Returns the constructor of the class. This is a {@link VirtualFunction} that can be called to
     * instantiate the class. Implementations may choose to provide a default constructor, or none
     * at all. If no (usable) constructor exists, this method will return {@code null}.
     *
     * @return The constructor of the class, or {@code null} if no constructor exists.
     */
    VirtualFunction constructor();

    /**
     * Returns the method with the given name. If no method with the given name exists, this method
     * will return {@code null}.
     *
     * @param name The name of the method.
     * @return The method with the given name, or {@code null} if no method with the given name
     */
    MethodReference method(String name);

    /**
     * Returns the super class of the class. If the class does not have a super class, this method
     * will return {@code null}.
     *
     * @return The super class of the class, or {@code null} if the class does not have a super class.
     */
    ClassReference superClass();

    /**
     * Returns the name of the class. This is the name of the class as it is known in runtime of the
     * script. If the class is defined in an import, and is aliased within the scope of the script,
     * the alias will be returned.
     *
     * @return The name of the class.
     */
    String name();

}
