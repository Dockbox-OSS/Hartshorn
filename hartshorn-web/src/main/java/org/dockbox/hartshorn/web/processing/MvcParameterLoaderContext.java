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

package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.web.mvc.ViewModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;

public class MvcParameterLoaderContext extends HttpRequestParameterLoaderContext{

    @Getter
    private final ViewModel viewModel;

    public MvcParameterLoaderContext(final MethodContext<?, ?> method, final TypeContext<?> type, final Object instance, final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response,
                                     final ViewModel viewModel) {
        super(method, type, instance, applicationContext, request, response);
        this.viewModel = viewModel;
    }
}
