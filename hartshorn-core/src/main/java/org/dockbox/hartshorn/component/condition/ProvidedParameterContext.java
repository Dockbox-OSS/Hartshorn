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

package org.dockbox.hartshorn.component.condition;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.reflect.ExecutableElementContext;
import org.dockbox.hartshorn.util.reflect.ParameterContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProvidedParameterContext extends DefaultContext {

    private final Map<ParameterContext<?>, Object> arguments = new ConcurrentHashMap<>();

    private ProvidedParameterContext(final Map<ParameterContext<?>, Object> arguments) {
        this.arguments.putAll(arguments);
    }

    public static ProvidedParameterContext of(final Map<ParameterContext<?>, Object> arguments) {
        return new ProvidedParameterContext(arguments);
    }

    public static ProvidedParameterContext of(final List<ParameterContext<?>> parameters, final List<Object> arguments) {
        if (parameters.size() != arguments.size()) {
            throw new IllegalArgumentException("Parameters and arguments must be of the same size");
        }
        final Map<ParameterContext<?>, Object> argumentMap = IntStream.range(0, parameters.size()).boxed()
                .collect(Collectors.toMap(parameters::get, arguments::get, (a, b) -> b, ConcurrentHashMap::new));

        return new ProvidedParameterContext(argumentMap);
    }

    public static ProvidedParameterContext of(final ExecutableElementContext<?, ?> executable, final List<Object> arguments) {
        return of(executable.parameters(), arguments);
    }

    public Map<ParameterContext<?>, Object> arguments() {
        return arguments;
    }
}
