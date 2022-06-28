package org.dockbox.hartshorn.hsl.callable.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.callable.ArityCheckingCallableNode;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.List;

public class VirtualFunction extends ArityCheckingCallableNode {

    public static final String CLASS_INIT = "init";
    private final FunctionStatement declaration;
    private final VariableScope closure;

    private final boolean isInitializer;

    public VirtualFunction(final FunctionStatement declaration, final VariableScope closure, final boolean isInitializer) {
        this.closure = closure;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
    }

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
            if (this.isInitializer) return this.closure.getAt(0, TokenType.THIS.representation());
            return returnValue.value();
        }
        if (this.isInitializer) return this.closure.getAt(0, TokenType.THIS.representation());
        return null;
    }
}
