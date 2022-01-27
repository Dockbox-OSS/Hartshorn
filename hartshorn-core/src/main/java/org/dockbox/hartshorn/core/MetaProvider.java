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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.context.element.AnnotatedElementContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.TypedOwner;

/**
 * The type responsible for providing metadata on given types.
 */
public interface MetaProvider {

    /**
     * Looks up the owner of the given type. If no explicit owner exists, the
     * type itself is used as owner.
     *
     * @param type The type to look up
     *
     * @return The owner of the type
     */
    TypedOwner lookup(TypeContext<?> type);

    /**
     * Looks up whether the given type is a singleton.
     *
     * @param type The type to look up
     *
     * @return <code>true</code> if the type is a singleton, or <code>false</code>
     */
    boolean singleton(TypeContext<?> type);
    boolean singleton(AnnotatedElementContext<?> element);

    /**
     * Looks up whether the given type is a component-like type. This only applies if
     * the dependency injection module is present, if it is not this should always
     * return <code>false</code>.
     *
     * @param type The type to look up
     *
     * @return <code>true</code> if the type is a component-like type, or <code>false</code>
     */
    boolean isComponent(TypeContext<?> type);

}
