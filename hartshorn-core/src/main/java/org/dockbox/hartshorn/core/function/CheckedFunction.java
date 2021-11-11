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

import org.dockbox.hartshorn.core.exceptions.ApplicationException;

/**
 * Extension of {@link java.util.function.Function} with the addition of a
 * <code>throws ApplicationException</code> clause.
 *
 * @param <T>
 *         the type of the first argument to the function
 * @param <R>
 *         the type of the result of the function
 *
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {

    R apply(T t) throws ApplicationException;
}
