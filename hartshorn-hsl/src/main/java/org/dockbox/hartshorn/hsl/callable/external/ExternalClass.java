package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.Constructor;
import java.util.List;

public class ExternalClass<T> extends ExternalExecutable<Constructor<T>, T, ConstructorContext<T>> {

    private final TypeContext<T> type;

    public ExternalClass(final Class<T> type) {
        this.type = TypeContext.of(type);
    }

    @Override
    public void verify(final Token at, final List<Object> arguments) {
        if (this.matchingExecutable(this.type.constructors(), arguments) != null) return;
        throw new RuntimeError(at, "No matching constructor for external class " + this.type.name() + " with arguments " + arguments);
    }

    @Override
    public Object call(final Interpreter interpreter, final List<Object> arguments) throws ApplicationException {
        final ConstructorContext<T> executable = this.matchingExecutable(this.type.constructors(), arguments);
        final T instance = executable.createInstance(arguments.toArray()).rethrowUnchecked().orNull();
        return new ExternalInstance(instance);
    }

    @Override
    public String toString() {
        return this.type.qualifiedName();
    }
}
