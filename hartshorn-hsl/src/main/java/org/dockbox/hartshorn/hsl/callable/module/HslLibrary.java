package org.dockbox.hartshorn.hsl.callable.module;

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.callable.ArityCheckingCallableNode;
import org.dockbox.hartshorn.hsl.callable.NativeExecutionException;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;
import java.util.Map;

public class HslLibrary extends ArityCheckingCallableNode {

    private final NativeFunctionStatement declaration;
    private final Map<String, NativeModule> externalModules;

    public HslLibrary(final NativeFunctionStatement declaration, final Map<String, NativeModule> externalModules) {
        this.declaration = declaration;
        this.externalModules = externalModules;
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final List<Object> arguments) throws NativeExecutionException {
        final String moduleName = this.declaration.moduleName().lexeme();
        final String functionName = this.declaration.name().lexeme();

        if (!this.externalModules.containsKey(moduleName)) {
            throw new NativeExecutionException("Module Loader : Can't find class with name : " + functionName);
        }

        final NativeModule module = this.externalModules.get(moduleName);
        return module.call(at, interpreter, functionName, arguments);
    }
}
