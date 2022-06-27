package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.callable.VerifiableCallableNode;
import org.dockbox.hartshorn.util.reflect.ExecutableElementContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.Executable;
import java.util.List;

public abstract class ExternalExecutable<A extends Executable, P, T extends ExecutableElementContext<A, P>> implements VerifiableCallableNode {

    protected T matchingExecutable(final List<T> executables, final List<Object> arguments) {
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
