/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.context.element;

/**
 * Acts as an extension of a given {@link QualifiedElement}, allowing you to modify protected values. Modifiers can not
 * modify private or final values. Any given modifier will only add or remove values like
 * {@link java.lang.annotation.Annotation annotations}, or change single non-final values. 
 @param <E>
 */
@FunctionalInterface
public interface ElementModifier<E extends QualifiedElement> {
    E element();
}
