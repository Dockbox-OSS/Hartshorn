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
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated(since = "4.2.5", forRemoval = true)
public interface RequestArgumentProcessor<A extends Annotation> {
    Class<A> annotation();
    default boolean preconditions(final ApplicationContext context, final ParameterContext<?> parameter, final HttpServletRequest request, final HttpServletResponse response) {
        return !(parameter.isVarargs() || parameter.type().isVoid());
    }
    <T> Exceptional<T> process(ApplicationContext context, ParameterContext<T> parameter, HttpServletRequest request, HttpServletResponse response);
}
