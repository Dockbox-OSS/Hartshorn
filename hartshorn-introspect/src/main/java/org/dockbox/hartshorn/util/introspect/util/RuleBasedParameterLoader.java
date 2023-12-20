/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

public class RuleBasedParameterLoader<C extends ParameterLoaderContext> extends ParameterLoader {

    private final Set<ParameterLoaderRule<C>> rules = ConcurrentHashMap.newKeySet();

    private final Class<C> contextType;

    public RuleBasedParameterLoader(Class<C> contextType) {
        this.contextType = contextType;
    }

    public RuleBasedParameterLoader<?> add(ParameterLoaderRule<? super C> rule) {
        this.rules.add((ParameterLoaderRule<C>) rule);
        return this;
    }

    protected Set<ParameterLoaderRule<C>> rules() {
        return this.rules;
    }

    @Override
    public boolean isCompatible(ParameterLoaderContext context) {
        return context != null && this.contextType.isAssignableFrom(context.getClass());
    }

    @Override
    public @Nullable Object loadArgument(ParameterLoaderContext context, int index, Object... args) {
        if (!this.isCompatible(context)) {
            return null;
        }
        Option<ParameterView<?>> parameterCandidate = context.executable().parameters().at(index);
        if (parameterCandidate.present()) {
            C adjustedContext = this.contextType.cast(context);
            ParameterView<?> parameter = parameterCandidate.get();
            return loadArgument(index, args, parameter, adjustedContext);
        }
        return null;
    }

    private Object loadArgument(int index, Object[] args, ParameterView<?> parameter, C adjustedContext) {
        for (ParameterLoaderRule<C> rule : this.rules()) {
            if (rule.accepts(parameter, index, adjustedContext, args)) {
                Option<?> argument = rule.load(parameter, index, adjustedContext, args);
                if (argument.present()) {
                    return argument.get();
                }
            }
        }
        return this.loadDefault(parameter, index, adjustedContext, args);
    }

    @Override
    public List<Object> loadArguments(ParameterLoaderContext context, Object... args) {
        if (!this.isCompatible(context)) {
            return List.of(args);
        }
        C adjustedContext = this.contextType.cast(context);
        List<Object> arguments = new ArrayList<>();
        List<ParameterView<?>> parameters = context.executable().parameters().all();
        parameters:
        for (int i = 0; i < parameters.size(); i++) {
            ParameterView<?> parameter = parameters.get(i);
            try {
                for (ParameterLoaderRule<C> rule : this.rules) {
                    if (rule.accepts(parameter, i, adjustedContext, args)) {
                        Option<?> argument = rule.load(parameter, i, adjustedContext, args);
                        arguments.add(argument.orNull());
                        continue parameters;
                    }
                }
                arguments.add(this.loadDefault(parameter, i, adjustedContext, args));
            }
            catch (ApplicationRuntimeException e) {
                throw new ParameterLoadException(parameter, e);
            }
        }
        return Collections.unmodifiableList(arguments);
    }

    protected <T> T loadDefault(ParameterView<T> parameter, int index, C context, Object... args) {
        return parameter.type().defaultOrNull();
    }
}
