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

package org.dockbox.hartshorn.core.boot;

public interface ExceptionHandler {
    void handle(Throwable throwable);
    void handle(String message, Throwable throwable);
    ExceptionHandler stacktraces(boolean stacktraces);

    /**
     * Throw the provided {@link Throwable} as if it were unchecked. This treats the exception as if it were unchecked,
     * and will not require it to be handled by javac. This is possible because of a side effect of type erasure, see
     * the link below for more details. The return result is only used to mimic a return value when it is needed, as the
     * <code>throws</code> cause is not enough to make the compiler know that the method will throw an exception.
     *
     * @param t The exception to throw.
     * @param <T> The type of the exception.
     * @throws T The exception to throw.
     * @see <a href="https://blog.jooq.org/throw-checked-exceptions-like-runtime-exceptions-in-java/">Throw checked exceptions like runtime exceptions in Java</a>
     */
    static <T extends Throwable, R> R unchecked(final Throwable t) throws T {
        throw (T) t;
    }
}
