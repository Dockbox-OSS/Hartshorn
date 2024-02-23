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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * An introspector is responsible for creating {@link TypeView}s from a given type reference. This is used to
 * introspect types in a generic way, without having to know the exact type of the type reference.
 *
 * @see TypeView
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ReferenceIntrospector {

    /**
     * Introspects the provided type reference and returns a {@link TypeView} that represents the type. If the type
     * represents a class that cannot be found, a {@link Void} type is returned.
     *
     * @param type the type reference to introspect
     * @return the type view that represents the type reference
     */
    TypeView<?> introspect(String type);

    /**
     * Introspects the provided type reference and returns a {@link TypeView} that represents the type. If the type
     * reference is a generic type, the type parameters are also introspected. If the type reference is a wildcard type,
     * a {@link org.dockbox.hartshorn.util.introspect.view.wildcard.WildcardTypeView} is returned. If the type reference
     * represents a class that cannot be found, a {@link Void} type is returned.
     *
     * @param reference the type reference to introspect
     * @return the type view that represents the type reference
     */
    TypeView<?> introspect(TypeReference reference);

    /**
     * Returns the environment that is used by this introspector.
     *
     * @return the environment that is used by this introspector
     */
    IntrospectionEnvironment environment();
}
