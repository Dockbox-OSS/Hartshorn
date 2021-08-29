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

package org.dockbox.hartshorn.di.context.element;

import java.lang.reflect.Parameter;

import lombok.Getter;

@SuppressWarnings("unchecked")
public class ParameterContext<T> extends AnnotatedElementContext<Parameter> {

    private final Parameter parameter;
    @Getter private final boolean isVarargs;

    private String name;
    private TypeContext<T> type;

    private ParameterContext(final Parameter parameter) {
        this.parameter = parameter;
        this.isVarargs = parameter.isVarArgs();
    }

    public static <T> ParameterContext<T> of(final Parameter parameter) {
        return new ParameterContext<>(parameter);
    }

    public String name() {
        if (this.name == null) {
            this.name = this.element().getName();
        }
        return this.name;
    }

    public TypeContext<T> type() {
        if (this.type == null) {
            this.type = TypeContext.of((Class<T>) this.element().getType());
        }
        return this.type;
    }

    @Override
    protected Parameter element() {
        return this.parameter;
    }
}
