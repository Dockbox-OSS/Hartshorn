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

package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;

public class RuleBasedParameterLoader<C extends ParameterLoaderContext> extends ParameterLoader<C>{

    @Getter(AccessLevel.PROTECTED)
    private final Set<ParameterLoaderRule<C>> rules = HartshornUtils.emptyConcurrentSet();

    public RuleBasedParameterLoader add(final ParameterLoaderRule<? super C> rule) {
        this.rules.add((ParameterLoaderRule<C>) rule);
        return this;
    }

    @Override
    public List<Object> loadArguments(final C context, final Object... args) {
        final List<Object> arguments = new ArrayList<>();
        final LinkedList<ParameterContext<?>> parameters = context.executable().parameters();
        parameters:
        for (int i = 0; i < parameters.size(); i++) {
            final ParameterContext<?> parameter = parameters.get(i);
            for (final ParameterLoaderRule<C> rule : this.rules) {
                if (rule.accepts(parameter, i, context, args)) {
                    final Exceptional<Object> argument = rule.load((ParameterContext<Object>) parameter, i, context, args);
                    arguments.add(argument.orNull());
                    continue parameters;
                }
            }
            arguments.add(this.loadDefault(parameter, i, context, args));
        }
        return Collections.unmodifiableList(arguments);
    }

    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final C context, final Object... args) {
        return parameter.type().defaultOrNull();
    }
}
