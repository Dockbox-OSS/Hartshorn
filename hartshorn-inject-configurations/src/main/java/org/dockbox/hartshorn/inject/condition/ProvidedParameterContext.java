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

package org.dockbox.hartshorn.inject.condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.dockbox.hartshorn.inject.DefaultFallbackCompatibleContext;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;

/**
 * A context that contains the arguments that were provided to a method or constructor. This context may
 * be used by {@link Condition}s to provide additional information about the context in which the condition
 * is evaluated.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public final class ProvidedParameterContext extends DefaultFallbackCompatibleContext {

    private final Map<ParameterView<?>, Object> arguments = new HashMap<>();

    private ProvidedParameterContext(Map<ParameterView<?>, Object> arguments) {
        this.arguments.putAll(arguments);
    }

    /**
     * Creates a new {@link ProvidedParameterContext} with the given arguments.
     *
     * @param arguments the arguments
     * @return the new context
     */
    public static ProvidedParameterContext of(Map<ParameterView<?>, Object> arguments) {
        return new ProvidedParameterContext(arguments);
    }

    /**
     * Creates a new {@link ProvidedParameterContext} with the given arguments. This method will create a mapping
     * between the given parameters and arguments, based on the order in which they are provided. The size of the
     * given lists must be equal.
     *
     * @param parameters the parameters
     * @param arguments the arguments
     *
     * @return the new context
     *
     * @throws IllegalArgumentException if the size of the given lists is not equal
     */
    public static ProvidedParameterContext of(List<ParameterView<?>> parameters, List<Object> arguments) {
        if (parameters.size() != arguments.size()) {
            throw new IllegalArgumentException("Parameters and arguments must be of the same size");
        }
        Map<ParameterView<?>, Object> argumentMap = IntStream.range(0, parameters.size()).boxed()
                .collect(
                        HashMap::new,
                        (parameterViews, index) -> parameterViews.put(parameters.get(index), arguments.get(index)),
                        Map::putAll
                );
        return new ProvidedParameterContext(argumentMap);
    }

    /**
     * Creates a new {@link ProvidedParameterContext} with the given arguments. This method will create a mapping
     * between the parameters of the given executable and the given arguments, based on the order in which they are
     * provided. The size of the given list and the number of parameters of the executable must be equal.
     *
     * @param executable the executable to use as a source for the parameters
     * @param arguments the arguments
     *
     * @return the new context
     *
     * @throws IllegalArgumentException if the size of the given list is not equal to the number of parameters of the
     *                                  executable
     */
    public static ProvidedParameterContext of(ExecutableElementView<?> executable, List<Object> arguments) {
        return of(executable.parameters().all(), arguments);
    }

    /**
     * Returns the arguments that were provided to the method or constructor. This represents the mapping between
     * parameters and arguments.
     *
     * @return the arguments
     */
    public Map<ParameterView<?>, Object> arguments() {
        return this.arguments;
    }
}
