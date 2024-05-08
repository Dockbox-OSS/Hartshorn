/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.modules;

import java.util.List;

import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.NativeExecutionException;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * A common library containing multiple {@link NativeFunctionStatement}s which can be
 * invoked at runtime. The module can optionally be loaded into the interpreter when a
 * {@link NativeFunctionStatement} or {@link ModuleStatement} is encountered.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface NativeModule extends ContextCarrier {

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
     * @throws ScriptEvaluationError If the native function throws an exception.
     */
    Object call(Token at, Interpreter interpreter, NativeFunctionStatement function, List<Object> arguments) throws NativeExecutionException;

    /**
     * Gets the supported functions of this module. Which functions are supported is determined by the
     * implementation, though it is typically expected that these methods are public.
     *
     * @param moduleName  The name of the module. This is used to identify the module.
     * @param interpreter
     * @return The supported functions of this module.
     */
    List<NativeFunctionStatement> supportedFunctions(Token moduleName, Interpreter interpreter);
}
