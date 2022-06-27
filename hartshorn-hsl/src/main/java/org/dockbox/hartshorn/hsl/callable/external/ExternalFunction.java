package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.Method;
import java.util.List;

public class ExternalFunction extends ExternalExecutable<Method, Object, MethodContext<?, Object>> {

    private final Object instance;
    private final String methodName;
    private final TypeContext<Object> type;

    public ExternalFunction(final Object instance, final String methodName) {
        this.instance = instance;
        this.methodName = methodName;
        this.type = TypeContext.of(instance);
    }

    public Object instance() {
        return this.instance;
    }

    public String methodName() {
        return this.methodName;
    }

    public TypeContext<Object> type() {
        return this.type;
    }

    @Override
    public void verify(final Token at, final List<Object> arguments) {
        this.method(at, arguments);
    }

    private MethodContext<?, Object> method(final Token at, final List<Object> arguments) {
        final Result<MethodContext<?, Object>> zeroParameterMethod = this.type.method(this.methodName);
        if (arguments.isEmpty() && zeroParameterMethod.present()) {
            return zeroParameterMethod.get();
        }
        final List<MethodContext<?, Object>> methods = this.type.methods().stream()
                .filter(m -> m.name().equals(this.methodName))
                .filter(m -> m.parameterCount() == arguments.size())
                .toList();
        if (methods.isEmpty()) {
            throw new RuntimeError(at, "Method '" + this.methodName + "' with " + arguments.size() + " parameters does not exist on external instance of type " + this.type.name());
        }

        final MethodContext<?, Object> executable = this.matchingExecutable(this.type.methods(), arguments);
        if (executable != null) return executable;

        throw new RuntimeError(at, "Method '" + this.methodName + "' with parameters accepting " + arguments + " does not exist on external instance of type " + this.type.name());
    }

    @Override
    public Object call(final Interpreter interpreter, final List<Object> arguments) throws ApplicationException {
        final MethodContext<?, Object> method = this.method(null, arguments);
        final Result<?> result = method.invoke(this.instance, arguments);
        if (result.caught()) {
            if (result.error() instanceof ApplicationException ae) throw ae;
            throw new ApplicationException(result.error());
        }
        return result.map(ExternalInstance::new).orNull();
    }

    @Override
    public String toString() {
        return this.type.qualifiedName() + "#" + this.methodName;
    }
}
