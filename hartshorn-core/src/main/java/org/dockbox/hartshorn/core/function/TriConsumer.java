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

package org.dockbox.hartshorn.core.function;

/**
 * Extension of {@link java.util.function.Consumer} and {@link java.util.function.BiConsumer} with
 * the addition of a third parameter of type <code>O</code>
 *
 * @param <T>
 *         the type of the first argument to the operation
 * @param <U>
 *         the type of the second argument to the operation
 * @param <O>
 *         the type of the third argument to the operation
 */
@FunctionalInterface
public interface TriConsumer<T, U, O> {
    void accept(T t, U u, O o);
}
