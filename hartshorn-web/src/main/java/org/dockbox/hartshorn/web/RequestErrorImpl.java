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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.DefaultCarrierContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
public class RequestErrorImpl extends DefaultCarrierContext implements RequestError {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final int statusCode;
    private final PrintWriter writer;
    private final Exceptional<Throwable> cause;
    @Setter private String message;
    @Setter private boolean yieldDefaults = false;

    public RequestErrorImpl(final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response, final int statusCode, final PrintWriter writer, final String message, final Throwable cause) {
        super(applicationContext);
        this.request = request;
        this.response = response;
        this.statusCode = statusCode;
        this.writer = writer;
        this.message = message;
        this.cause = Exceptional.of(cause, cause);
    }
}
