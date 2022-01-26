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

package org.dockbox.hartshorn.core.task.pipeline.exceptions;

/**
 * @deprecated Moved to https://github.com/GuusLieben/JPipelines
 */
@Deprecated(forRemoval = true, since = "22.2")
public class IllegalPipelineException extends RuntimeException {

    public IllegalPipelineException(final String errorMessage, final Throwable throwable) {
        super(errorMessage, throwable);
    }

    public IllegalPipelineException(final String errorMessage) {
        super(errorMessage);
    }
}
