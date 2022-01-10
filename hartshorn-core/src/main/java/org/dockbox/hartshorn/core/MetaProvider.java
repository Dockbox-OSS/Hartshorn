/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.domain.TypedOwner;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

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
    boolean singleton(MethodContext<?, ?> method);

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
