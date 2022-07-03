package org.dockbox.hartshorn.hsl.callable.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.callable.CallableNode;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.List;

/**
 * Represents a function definition inside a script. The function is identified by its name, and
 * parameters. The function can carry a variety of additional information such as the body, and
 * its body.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class VirtualFunction implements CallableNode {

    public static final String CLASS_INIT = "init";
    private final FunctionStatement declaration;
    private final VariableScope closure;

    private final boolean isInitializer;

    public VirtualFunction(final FunctionStatement declaration, final VariableScope closure, final boolean isInitializer) {
        this.closure = closure;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
    }

    /**
     * Creates a new {@link VirtualFunction} bound to the given instance. This will cause
     * the function to use the given instance when invoking.
     * @param instance The instance to bind to.
     * @return A new {@link VirtualFunction} bound to the given instance.
     */
    public VirtualFunction bind(final VirtualInstance instance) {
        final VariableScope variableScope = new VariableScope(this.closure);
        variableScope.define(TokenType.THIS.representation(), instance);
        return new VirtualFunction(this.declaration, variableScope, this.isInitializer);
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final List<Object> arguments) {
        final VariableScope variableScope = new VariableScope(this.closure);
        for (int i = 0; i < this.declaration.parameters().size(); i++) {
            variableScope.define(this.declaration.parameters().get(i).lexeme(), arguments.get(i));
        }
        try {
            interpreter.execute(this.declaration.functionBody(), variableScope);
        }
        catch (final Return returnValue) {
            if (this.isInitializer) return this.closure.getAt(at, 0, TokenType.THIS.representation());
            return returnValue.value();
        }
        if (this.isInitializer) return this.closure.getAt(at, 0, TokenType.THIS.representation());
        return null;
    }
}
