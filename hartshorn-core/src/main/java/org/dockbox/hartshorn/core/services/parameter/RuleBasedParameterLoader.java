/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
