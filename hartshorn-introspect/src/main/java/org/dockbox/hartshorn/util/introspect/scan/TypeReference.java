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

package org.dockbox.hartshorn.util.introspect.scan;

/**
 * A reference to a {@link Class} that can be used to load the class, or to obtain information about the class.
 * Implementations of this interface are expected to be immutable.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public interface TypeReference {

    /**
     * Loads the class that is referenced by this instance. Where possible, this should not initialize
     * the class.
     *
     * @param classLoader The class loader to use to load the class.
     *
     * @return The class that is referenced by this instance.
     * @throws ClassReferenceLoadException When the class cannot be loaded.
     */
    Class<?> getOrLoad(ClassLoader classLoader) throws ClassReferenceLoadException;

    /**
     * Returns the fully qualified name of the class that is referenced by this instance.
     *
     * @return The fully qualified name of the class that is referenced by this instance.
     */
    String qualifiedName();

    /**
     * Returns the simple name of the class that is referenced by this instance.
     *
     * @return The simple name of the class that is referenced by this instance.
     */
    String simpleName();

    /**
     * Returns the name of the package that contains the class that is referenced by this instance.
     *
     * @return The name of the package that contains the class that is referenced by this instance.
     */
    String packageName();
}
