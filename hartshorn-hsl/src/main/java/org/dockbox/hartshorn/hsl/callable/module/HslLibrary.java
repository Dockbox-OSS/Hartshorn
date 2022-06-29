package org.dockbox.hartshorn.hsl.callable.module;

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.callable.CallableNode;
import org.dockbox.hartshorn.hsl.callable.NativeExecutionException;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;
import java.util.Map;

/**
 * A common library containing multiple {@link NativeModule native modules} which can be
 * used to invoke native functions. The library is loaded into the interpreter when a
 * {@link NativeFunctionStatement} or {@link ModuleStatement} is encountered.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class HslLibrary implements CallableNode {

    private final NativeFunctionStatement declaration;
    private final Map<String, NativeModule> externalModules;

    public HslLibrary(final NativeFunctionStatement declaration, final Map<String, NativeModule> externalModules) {
        this.declaration = declaration;
        this.externalModules = externalModules;
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final List<Object> arguments) throws NativeExecutionException {
        final String moduleName = this.declaration.moduleName().lexeme();

        if (!this.externalModules.containsKey(moduleName)) {
            throw new NativeExecutionException("Module Loader : Can't find class with name : " + moduleName);
        }

        final NativeModule module = this.externalModules.get(moduleName);
        return module.call(at, interpreter, this.declaration, arguments);
    }
}
