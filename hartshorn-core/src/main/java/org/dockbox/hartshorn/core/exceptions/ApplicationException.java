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

    public RuntimeException runtime() {
        return new RuntimeException(this);
    }

    public Throwable unwrap() {
        Throwable root = this;
        while (root.getCause() instanceof ApplicationException applicationException && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }
}
