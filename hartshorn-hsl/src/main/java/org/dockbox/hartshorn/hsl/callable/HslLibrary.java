package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;

import java.util.List;
import java.util.Map;

public class HslLibrary extends ArityCheckingCallable {

    private final NativeFunctionStatement declaration;
    private final Map<String, NativeModule> externalModules;

    public HslLibrary(final NativeFunctionStatement declaration, final Map<String, NativeModule> externalModules) {
        this.declaration = declaration;
        this.externalModules = externalModules;
    }

    @Override
    public int arity() {
        return this.declaration.params().size();
    }

    @Override
    public Object call(final Interpreter interpreter, final List<Object> arguments) throws NativeExecutionException {
        final String moduleName = this.declaration.moduleName().lexeme();
        final String functionName = this.declaration.name().lexeme();

        if (!this.externalModules.containsKey(moduleName)) {
            throw new NativeExecutionException("Module Loader : Can't find class with name : " + functionName);
        }

        final NativeModule module = this.externalModules.get(moduleName);
        return module.call(interpreter, functionName, arguments);
    }
}
