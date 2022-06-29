package org.dockbox.hartshorn.hsl.callable.module;

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.callable.NativeExecutionException;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;

/**
 * A common library containing multiple {@link NativeFunctionStatement}s which can be
 * invoked at runtime. The module can optionally be loaded into the interpreter when a
 * {@link NativeFunctionStatement} or {@link ModuleStatement} is encountered.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public interface NativeModule {

    /**
     * Call the native function with the given name and arguments. The arguments are passed as a list of objects,
     * which can be of any type. The return value is converted to an object of the same type as the return type of the
     * native function.
     *
     * @param at The token at which the call is made. This is used for error reporting.
     * @param interpreter The interpreter in which the call is made. This can be used to obtain additional context.
     * @param function The native function to call.
     * @param arguments The arguments to pass to the native function.
     * @return The return value of the native function.
     * @throws NativeExecutionException If the native function can not be invoked.
     * @throws RuntimeError If the native function throws an exception.
     */
    Object call(Token at, Interpreter interpreter, NativeFunctionStatement function, List<Object> arguments) throws NativeExecutionException;

    /**
     * Gets the supported functions of this module. Which functions are supported is determined by the
     * implementation, though it is typically expected that these methods are public.
     *
     * @param moduleName The name of the module. This is used to identify the module.
     * @return The supported functions of this module.
     */
    List<NativeFunctionStatement> supportedFunctions(Token moduleName);
}
