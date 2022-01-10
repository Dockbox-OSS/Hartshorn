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

import org.dockbox.hartshorn.core.boot.HartshornExceptionHandler;

import lombok.Getter;

@Getter
public class TestExceptionHandle extends HartshornExceptionHandler {

    private boolean stacktrace;
    private String message;
    private Throwable exception;

    @Override
    public void handle(final String message, final Throwable throwable) {
        this.message = message;
        this.exception = throwable;
    }

    @Override
    public TestExceptionHandle stacktraces(final boolean stacktraces) {
        this.stacktrace = stacktraces;
        return this;
    }
}
