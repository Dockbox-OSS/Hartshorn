/*
 * Copyright 2019-2024 the original author or authors.
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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link ParameterLoader} that loads parameters based on a set of rules. The rules are evaluated in no
 * particular order, and the first rule that accepts the parameter will be used to load the parameter. In
 * case no rule accepts the parameter, the default value of the parameter type will be used.
 *
 * <p>As there is no particular order in which the rules are evaluated, it remains the responsibility of
 * the caller to ensure that the rules are not conflicting.
 *
 * @param <C> the context type that is used to provide context to the rules
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public class RuleBasedParameterLoader<C extends ParameterLoaderContext> implements ParameterLoader {

    private final Set<ParameterLoaderRule<C>> rules = ConcurrentHashMap.newKeySet();

    private final Class<C> contextType;

    public RuleBasedParameterLoader(Class<C> contextType) {
        this.contextType = contextType;
    }

    public static RuleBasedParameterLoader<ParameterLoaderContext> createDefault() {
        return create(ParameterLoaderContext.class);
    }

    public static <C extends ParameterLoaderContext> RuleBasedParameterLoader<C> create(Class<C> contextType) {
        return new RuleBasedParameterLoader<>(contextType);
    }

    /**
     * Adds the provided rule to the set of rules that are used to load parameters.
     *
     * @param rule the rule to add
     * @return the current instance
     */
    public RuleBasedParameterLoader<?> add(ParameterLoaderRule<? super C> rule) {
        this.rules.add((ParameterLoaderRule<C>) rule);
        return this;
    }

    /**
     * Returns an unmodifiable set of rules that are used to load parameters.
     *
     * @return an unmodifiable set of rules that are used to load parameters
     */
    public Set<ParameterLoaderRule<C>> rules() {
        return Set.copyOf(this.rules);
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
            return this.loadArgument(index, args, parameter, adjustedContext);
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

        for (int i = 0; i < parameters.size(); i++) {
            ParameterView<?> parameter = parameters.get(i);
            Object argument = this.loadArgument(i, args, parameter, adjustedContext);
            arguments.add(argument);
        }
        return Collections.unmodifiableList(arguments);
    }

    /**
     * Loads the default value for the provided parameter. This method is invoked when no rule accepts the
     * parameter.
     *
     * @param parameter the parameter to load the default value for
     * @param index the index of the parameter in the parameter list
     * @param context the context to use when looking up the parameter value
     * @param args the original arguments that are passed to the method that is being invoked
     * @param <T> the type of the parameter
     *
     * @return the default value for the provided parameter
     */
    protected <T> T loadDefault(ParameterView<T> parameter, int index, C context, Object... args) {
        return parameter.type().defaultOrNull();
    }
}
