package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;

public interface NativeModule {
    Object call(Interpreter interpreter, String function, List<Object> arguments) throws NativeExecutionException;

    List<NativeFunctionStatement> supportedFunctions(Token moduleName);
}
