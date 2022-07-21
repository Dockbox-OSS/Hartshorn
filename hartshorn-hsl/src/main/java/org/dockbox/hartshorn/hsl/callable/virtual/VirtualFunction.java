/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.callable.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.callable.CallableNode;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
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
        final List<Token> parameters = this.declaration.parameters();
        if (parameters.size() != arguments.size()) {
            throw new RuntimeError(at, "Expected %d %s, but got %d".formatted(
                    parameters.size(),
                    (parameters.size() == 1 ? "argument" : "arguments"),
                    arguments.size()));
        }
        for (int i = 0; i < parameters.size(); i++) {
            variableScope.define(parameters.get(i).lexeme(), arguments.get(i));
        }
        try {
            interpreter.execute(this.declaration.statements(), variableScope);
        }
        catch (final Return returnValue) {
            if (this.isInitializer) return this.closure.getAt(at, 0, TokenType.THIS.representation());
            return returnValue.value();
        }
        if (this.isInitializer) return this.closure.getAt(at, 0, TokenType.THIS.representation());
        return null;
    }
}
