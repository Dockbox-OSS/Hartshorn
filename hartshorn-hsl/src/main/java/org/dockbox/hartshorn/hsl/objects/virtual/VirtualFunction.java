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

package org.dockbox.hartshorn.hsl.objects.virtual;

import java.util.List;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.AbstractFinalizable;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;

/**
 * Represents a function definition inside a script. The function is identified by its name, and
 * parameters. The function can carry a variety of additional information such as the body, and
 * its body.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class VirtualFunction extends AbstractFinalizable implements MethodReference {

    private final ParametricExecutableStatement declaration;
    private final VariableScope closure;
    private final InstanceReference instance;
    private final boolean isInitializer;

    public VirtualFunction(ParametricExecutableStatement declaration, VariableScope closure, boolean isInitializer) {
        this(declaration, closure, null, isInitializer);
    }

    public VirtualFunction(ParametricExecutableStatement declaration, VariableScope closure, InstanceReference instance, boolean isInitializer) {
        super(declaration.isFinal());
        this.declaration = declaration;
        this.closure = closure;
        this.instance = instance;
        this.isInitializer = isInitializer;
    }

    /**
     * Creates a new {@link VirtualFunction} bound to the given instance. This will cause
     * the function to use the given instance when invoking.
     * @param instance The instance to bind to.
     * @return A new {@link VirtualFunction} bound to the given instance.
     */
    @Override
    public VirtualFunction bind(InstanceReference instance) {
        VariableScope variableScope = new VariableScope(this.closure);
        variableScope.define(ObjectTokenType.THIS.representation(), instance);
        return new VirtualFunction(this.declaration, variableScope, this.isInitializer);
    }

    @Override
    public Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments) {
        VariableScope variableScope = new VariableScope(this.closure);
        List<Parameter> parameters = this.declaration.parameters();
        if (parameters.size() != arguments.size()) {
            throw new ScriptEvaluationError("Expected %d %s, but got %d".formatted(
                    parameters.size(),
                    (parameters.size() == 1 ? "argument" : "arguments"),
                    arguments.size()),
                    Phase.INTERPRETING, at);
        }
        for (int i = 0; i < parameters.size(); i++) {
            variableScope.define(parameters.get(i).name().lexeme(), arguments.get(i));
        }
        try {
            interpreter.execute(this.declaration.statements(), variableScope);
        }
        catch (Return returnValue) {
            if (this.isInitializer) {
                return this.closure.getAt(at, 0, ObjectTokenType.THIS.representation());
            }
            return returnValue.value();
        }
        if (this.isInitializer) {
            return this.closure.getAt(at, 0, ObjectTokenType.THIS.representation());
        }
        return null;
    }

    @Override
    public InstanceReference bound() {
        return this.instance;
    }
}
