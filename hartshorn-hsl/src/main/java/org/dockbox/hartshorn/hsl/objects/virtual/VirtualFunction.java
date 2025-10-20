/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.AbstractFinalizable;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.runtime.Yield;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;

import java.util.List;

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
    private final ReturnStatement.ReturnType returnType;
    private final boolean isInitializer;

    public VirtualFunction(ParametricExecutableStatement declaration, VariableScope closure, boolean isInitializer) {
        this(declaration, closure, (InstanceReference) null, isInitializer);
    }

    public VirtualFunction(ParametricExecutableStatement declaration, VariableScope closure, InstanceReference instance, boolean isInitializer) {
        this(declaration, closure, instance, ReturnStatement.ReturnType.RETURN, isInitializer);
    }


    public VirtualFunction(ParametricExecutableStatement declaration, VariableScope closure, ReturnStatement.ReturnType returnType, boolean isInitializer) {
        this(declaration, closure, null, returnType, isInitializer);
    }

    public VirtualFunction(ParametricExecutableStatement declaration, VariableScope closure, InstanceReference instance, ReturnStatement.ReturnType returnType, boolean isInitializer) {
        super(declaration.isFinal());
        this.declaration = declaration;
        this.closure = closure;
        this.instance = instance;
        this.returnType = returnType;
        this.isInitializer = isInitializer;
    }

    /**
     * Gets the declaration of this function.
     * @return The declaration of this function.
     */
    public ParametricExecutableStatement declaration() {
        return this.declaration;
    }

    /**
     * Gets the closure of this function. The closure is the variable scope of the functio body.
     *
     * @return The closure of this function.
     */
    public VariableScope closure() {
        return this.closure;
    }

    /**
     * Gets the instance this function is bound to, or null if it is not bound.
     * @return The instance this function is bound to, or null if it is not bound.
     */
    public InstanceReference instance() {
        return this.instance;
    }

    /**
     * Gets the return type of this function.
     * @return The return type of this function.
     */
    public ReturnStatement.ReturnType returnType() {
        return returnType;
    }

    /**
     * Checks whether this function is an initializer. An initializer is a special type of function
     * that is called when an instance of a class is created. Initializers always return the
     * instance they are called on, and thus do not need an explicit return statement.
     *
     * @return True if this function is an initializer, false otherwise.
     */
    public boolean isInitializer() {
        return this.isInitializer;
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
        return new VirtualFunction(this.declaration, variableScope, instance, this.returnType, this.isInitializer);
    }

    @Override
    public Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments) {
        VariableScope variableScope = new VariableScope(this.closure);
        List<Parameter> parameters = this.declaration.parameters();
        if (parameters.size() != arguments.size()) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.EXPECTED_X_OF_Y_AT_Z,
                            parameters.size(),
                            (parameters.size() == 1 ? "argument" : "arguments"),
                            arguments.size()
                    ).at(at)
                    .build();
        }
        for (int i = 0; i < parameters.size(); i++) {
            variableScope.define(parameters.get(i).name().lexeme(), arguments.get(i));
        }
        try {
            interpreter.execute(this.declaration.statements(), variableScope);
        }
        catch (Yield yieldValue) {
            if (this.returnType != ReturnStatement.ReturnType.YIELD) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                        .message(DiagnosticMessage.ILLEGAL_YIELD_IN_NON_GENERATOR)
                        .at(at)
                        .build();
            }
            return yieldValue.value();
        }
        catch (Return returnValue) {
            if (this.returnType != ReturnStatement.ReturnType.RETURN) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                        .message(DiagnosticMessage.ILLEGAL_RETURN_IN_GENERATOR)
                        .at(at)
                        .build();
            }
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
