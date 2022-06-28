package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.ExecutableElementContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.Executable;
import java.util.List;

public class ExecutableLookup {

    public static <T> MethodContext<?, T> method(final Token at, final TypeContext<T> declaring, final String function, List<Object> arguments) {
        final Result<MethodContext<?, T>> zeroParameterMethod = declaring.method(function);
        if (arguments.isEmpty() && zeroParameterMethod.present()) {
            return zeroParameterMethod.get();
        }
        final List<MethodContext<?, T>> methods = declaring.methods().stream()
                .filter(m -> m.name().equals(function))
                .filter(m -> m.parameterCount() == arguments.size())
                .toList();
        if (methods.isEmpty()) {
            throw new RuntimeError(at, "Method '" + function + "' with " + arguments.size() + " parameters does not exist on external instance of type " + declaring.name());
        }

        final MethodContext<?, T> executable = executable(methods, arguments);
        if (executable != null) return executable;

        throw new RuntimeError(at, "Method '" + function + "' with parameters accepting " + arguments + " does not exist on external instance of type " + declaring.name());
    }

    public static <A extends Executable, P, T extends ExecutableElementContext<A, P>> T executable(final List<T> executables, final List<Object> arguments) {
        for (final T executable : executables) {
            boolean pass = true;
            if (executable.parameterCount() != arguments.size()) continue;
            for (int i = 0; i < executable.parameterTypes().size(); i++) {
                final TypeContext<?> parameter = executable.parameterTypes().get(i);
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
