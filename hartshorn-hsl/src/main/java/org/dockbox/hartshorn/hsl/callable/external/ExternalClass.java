package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.callable.CallableNode;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.List;
import java.util.Map;

/**
 * Represents a Java class that can be called from an HSL runtime. This class can be
 * used to create a new instance of the class. This requires the class to be imported
 * by the responsible {@link StandardRuntime} through {@link StandardRuntime#imports(Map)}.
 *
 * <pre>{@code
 * AbstractHslRuntime runtime = ...;
 * runtime.imports(Map.of("MyClass", MyClass.class));
 * runtime.run("var instance = MyClass();");
 * }</pre>
 *
 * @param <T> The type of the class.
 * @author Guus Lieben
 * @since 22.4
 */
public class ExternalClass<T> implements CallableNode {

    private final TypeContext<T> type;

    public ExternalClass(final Class<T> type) {
        this.type = TypeContext.of(type);
    }

    /**
     * Gets the {@link TypeContext} represented by this instance.
     * @return The {@link TypeContext} represented by this instance.
     */
    public TypeContext<T> type() {
        return this.type;
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final List<Object> arguments) throws ApplicationException {
        final ConstructorContext<T> executable = ExecutableLookup.executable(this.type.constructors(), arguments);
        final T instance = executable.createInstance(arguments.toArray()).rethrowUnchecked().orNull();
        return new ExternalInstance(instance);
    }

    @Override
    public String toString() {
        return this.type.qualifiedName();
    }
}
