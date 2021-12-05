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

package org.dockbox.hartshorn.core.exceptions;

import org.dockbox.hartshorn.core.boot.ExceptionHandler;

/**
 * Thrown when the application runs a validated problem. Typically, this means an
 * exception was caught and is rethrown as a {@link ApplicationException} with the
 * original exception as cause.
 */
public class ApplicationException extends Exception {

    public ApplicationException(final String message) {
        super(message);
    }

    public ApplicationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(final Throwable cause) {
        super(cause);
    }

    /**
     * @return The current exception, wrapped in a {@link RuntimeException}.
     * @deprecated Use {@link ExceptionHandler#unchecked(Throwable)} instead.
     */
    @Deprecated(since = "4.2.5", forRemoval = true)
    public RuntimeException runtime() {
        return new RuntimeException(this);
    }

    /**
     * Attempts to look up the first cause of the exception. If no cause is found,
     * the exception itself is returned.
     *
     * @return The first cause of the exception.
     */
    public Throwable unwrap() {
        Throwable root = this;
        while (root.getCause() instanceof ApplicationException && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }
}
