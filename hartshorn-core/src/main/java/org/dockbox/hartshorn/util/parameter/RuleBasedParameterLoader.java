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

package org.dockbox.hartshorn.util.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

public class RuleBasedParameterLoader<C extends ParameterLoaderContext> extends ParameterLoader<C>{

    private final Set<ParameterLoaderRule<C>> rules = ConcurrentHashMap.newKeySet();

    public RuleBasedParameterLoader<?> add(final ParameterLoaderRule<? super C> rule) {
        this.rules.add((ParameterLoaderRule<C>) rule);
        return this;
    }

    protected Set<ParameterLoaderRule<C>> rules() {
        return this.rules;
    }

    @Override
    public Object loadArgument(final C context, final int index, final Object... args) {
        final Option<ParameterView<?>> parameterCandidate = context.executable().parameters().at(index);
        if (parameterCandidate.present()) {
            final ParameterView<?> parameter = parameterCandidate.get();
            for (final ParameterLoaderRule<C> rule : this.rules()) {
                if (rule.accepts(parameter, index, context, args)) {
                    final Option<?> argument = rule.load(parameter, index, context, args);
                    if (argument.present()) return argument.get();
                }
            }
            return this.loadDefault(parameter, index, context, args);
        }
        return null;
    }

    @Override
    public List<Object> loadArguments(final C context, final Object... args) {
        final List<Object> arguments = new ArrayList<>();
        final List<ParameterView<?>> parameters = context.executable().parameters().all();
        parameters:
        for (int i = 0; i < parameters.size(); i++) {
            final ParameterView<?> parameter = parameters.get(i);
            for (final ParameterLoaderRule<C> rule : this.rules) {
                if (rule.accepts(parameter, i, context, args)) {
                    final Option<?> argument = rule.load(parameter, i, context, args);
                    arguments.add(argument.orNull());
                    continue parameters;
                }
            }
            arguments.add(this.loadDefault(parameter, i, context, args));
        }
        return Collections.unmodifiableList(arguments);
    }

    protected <T> T loadDefault(final ParameterView<T> parameter, final int index, final C context, final Object... args) {
        return parameter.type().defaultOrNull();
    }
}
