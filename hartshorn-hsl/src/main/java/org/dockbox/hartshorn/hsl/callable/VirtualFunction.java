package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.ast.FunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Environment;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.Return;

import java.util.List;

public class VirtualFunction implements HslCallable {

    private final FunctionStatement declaration;
    private final Environment closure;

    private final boolean isInitializer;

    public VirtualFunction(final FunctionStatement declaration, final Environment closure, final boolean isInitializer) {
        this.closure = closure;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
    }

    @Override
    public int arity() {
        return this.declaration.getParams().size();
    }

    public VirtualFunction bind(final VirtualInstance instance) {
        final Environment environment = new Environment(this.closure);
        environment.define("this", instance);
        return new VirtualFunction(this.declaration, environment, this.isInitializer);
    }

    @Override
    public Object call(final Interpreter interpreter, final List<Object> arguments) {
        final Environment environment = new Environment(this.closure);
        for (int i = 0; i < this.declaration.getParams().size(); i++) {
            environment.define(this.declaration.getParams().get(i).lexeme(), arguments.get(i));
        }
        try {
            interpreter.execute(this.declaration.getFunctionBody(), environment);
        }
        catch (final Return returnValue) {
            if (this.isInitializer) return this.closure.getAt(0, "this");
            return returnValue.getValue();
        }
        if (this.isInitializer) return this.closure.getAt(0, "this");
        return null;
    }
}
