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

package org.dockbox.hartshorn.hsl.objects.external;

import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;

/**
 * Utility class to lookup an executable by name and a list of arguments. The lookup may either use
 * a predefined list of executables, or by matching methods with the given name and argument types.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class ExecutableLookup {

    /**
     * Lookup a method by name and a list of arguments. This will first try to get all methods of
     * the declaring type, and filter them based on the given name. If the list of arguments is
     * empty, the method will be looked up without arguments. If the list of arguments is not empty,
     * the method will be looked up with the given arguments through {@link #executable(List, List)}.
     *
     * @param at The token at which the lookup is performed. This is used for error reporting.
     * @param declaring The declaring type of the method.
     * @param function The name of the method.
     * @param arguments The list of arguments.
     * @return The found executable.
     * @param <T> The type of the declaring type.
     */
    public static <T> MethodView<T, ?> method(final Token at, final TypeView<T> declaring, final String function, final List<Object> arguments) {
        final Result<MethodView<T, ?>> zeroParameterMethod = declaring.methods().named(function);
        if (arguments.isEmpty() && zeroParameterMethod.present()) {
            return zeroParameterMethod.get();
        }
        final List<MethodView<T, ?>> methods = declaring.methods().all().stream()
                .filter(m -> m.name().equals(function))
                .filter(m -> m.parameters().count() == arguments.size())
                .toList();
        if (methods.isEmpty()) {
            throw new RuntimeError(at, "Method '" + function + "' with " + arguments.size() + " parameters does not exist on external instance of type " + declaring.name());
        }

        final MethodView<T, ?> executable = executable(methods, arguments);
        if (executable != null) return executable;

        throw new RuntimeError(at, "Method '" + function + "' with parameters accepting " + arguments + " does not exist on external instance of type " + declaring.name());
    }

    /**
     * Lookup an executable based on a list of arguments. This will filter the list of executables based on
     * the number of arguments. If the amount of parameters matches the amount of arguments, the parameter
     * types are checked against the argument types. If the arguments are compatible, the executable is returned.
     *
     * @param executables The list of executables.
     * @param arguments The list of arguments.
     * @return The found executable.
     * @param <P> The type of the declaring parent of the executable.
     * @param <T> The context type representing the executable.
     */
    public static <P, T extends ExecutableElementView<P>> T executable(final List<T> executables, final List<Object> arguments) {
        for (final T executable : executables) {
            boolean pass = true;
            if (executable.parameters().count() != arguments.size()) continue;
            for (int i = 0; i < executable.parameters().count(); i++) {
                final TypeView<?> parameter = executable.parameters().types().get(i);
                final Object argument = arguments.get(i);
                if (!parameter.isInstance(argument)) {
                    pass = false;
                    break;
                }
            }
            if (pass) return executable;
        }
        return null;
    }
}
