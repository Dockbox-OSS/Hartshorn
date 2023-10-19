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

import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RuleBasedParameterLoader<C extends ParameterLoaderContext> extends ParameterLoader<C> {

    private final Set<ParameterLoaderRule<C>> rules = ConcurrentHashMap.newKeySet();

    public RuleBasedParameterLoader<?> add(ParameterLoaderRule<? super C> rule) {
        this.rules.add((ParameterLoaderRule<C>) rule);
        return this;
    }

    protected Set<ParameterLoaderRule<C>> rules() {
        return this.rules;
    }

    @Override
    public Object loadArgument(C context, int index, Object... args) {
        Option<ParameterView<?>> parameterCandidate = context.executable().parameters().at(index);
        if (parameterCandidate.present()) {
            ParameterView<?> parameter = parameterCandidate.get();
            for (ParameterLoaderRule<C> rule : this.rules()) {
                if (rule.accepts(parameter, index, context, args)) {
                    Option<?> argument = rule.load(parameter, index, context, args);
                    if (argument.present()) {
                        return argument.get();
                    }
                }
            }
            return this.loadDefault(parameter, index, context, args);
        }
        return null;
    }

    @Override
    public List<Object> loadArguments(C context, Object... args) {
        List<Object> arguments = new ArrayList<>();
        List<ParameterView<?>> parameters = context.executable().parameters().all();
        parameters:
        for (int i = 0; i < parameters.size(); i++) {
            ParameterView<?> parameter = parameters.get(i);
            try {
                for (ParameterLoaderRule<C> rule : this.rules) {
                    if (rule.accepts(parameter, i, context, args)) {
                        Option<?> argument = rule.load(parameter, i, context, args);
                        arguments.add(argument.orNull());
                        continue parameters;
                    }
                }
                arguments.add(this.loadDefault(parameter, i, context, args));
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
