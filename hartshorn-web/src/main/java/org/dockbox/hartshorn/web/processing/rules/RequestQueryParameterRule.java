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

package org.dockbox.hartshorn.web.processing.rules;

import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.AnnotatedParameterLoaderRule;
import org.dockbox.hartshorn.web.annotations.RequestParam;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

public class RequestQueryParameterRule extends AnnotatedParameterLoaderRule<RequestParam, HttpRequestParameterLoaderContext> {

    @Override
    protected Class<RequestParam> annotation() {
        return RequestParam.class;
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        return Exceptional.of(() -> {
            final RequestParam requestParam = parameter.annotation(RequestParam.class).get();
            String value = context.request().getParameter(requestParam.value());
            if (value == null) value = requestParam.or();

            if (parameter.type().is(String.class)) return (T) value;
            else if (parameter.type().isPrimitive()) {
                return TypeContext.toPrimitive(parameter.type(), value);
            }
            return null;
        });
    }
}
