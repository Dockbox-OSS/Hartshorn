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

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.stream.Collectors;

public abstract class ExecutableElementContext<A extends Executable> extends AnnotatedMemberContext<A> {

    private LinkedList<ParameterContext<?>> parameters;

    public LinkedList<ParameterContext<?>> parameters() {
        if (this.parameters == null) {
            final LinkedList<ParameterContext<?>> parameters = new LinkedList<>();
            for (final Parameter parameter : this.element().getParameters()) {
                parameters.add(ParameterContext.of(parameter));
            }
            this.parameters = parameters;
        }
        return this.parameters;
    }

    public LinkedList<TypeContext<?>> parameterTypes() {
        return this.parameters().stream().map(ParameterContext::type).collect(Collectors.toCollection(LinkedList::new));
    }

    public int parameterCount() {
        return this.element().getParameterCount();
    }
}
