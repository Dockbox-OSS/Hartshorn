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

import java.util.ArrayList;
import java.util.List;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.NativeExecutionException;
import org.dockbox.hartshorn.hsl.objects.external.ExecutableLookup;
import org.dockbox.hartshorn.hsl.objects.external.ExternalInstance;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents one or more Java methods that can be called from an HSL runtime. The methods
 * are pre-filtered based on their access-level. The methods are provided as {@link NativeFunctionStatement}s.
 *
 * <p>Methods can be executed based on the supported functions of the module. If the method
 * is not known at compile-time, it is looked up at run-time. If the method is not found, is
 * not accessible, or if it is not supported by the module, a {@link ScriptEvaluationError} is thrown.
 *
 * <p>If the method is not accessible, or any cannot be invoked, a {@link NativeExecutionException}
 * is thrown. For all other errors, a {@link ScriptEvaluationError} is thrown.
 *
 * <p>All execution calls are performed on the instance provided by {@link #instance()}. If the instance
 * is {@code null}, the method must be static.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public abstract class AbstractNativeModule implements NativeModule {

    private List<NativeFunctionStatement> supportedFunctions;

    /**
     * Gets the type of the module class that is being represented.
     * @return The type of the module class.
     */
    protected abstract Class<?> moduleClass();

    /**
     * Gets or creates the instance of the module class. This instance is used to invoke the methods.
     * @return The instance of the module class.
     */
    protected abstract Object instance();

    @Override
    public Object call(Token at, Interpreter interpreter, NativeFunctionStatement function, List<Object> arguments) throws NativeExecutionException {
        TypeView<?> typeView = this.applicationContext().environment().introspector().introspect(this.moduleClass());
        TypeView<Object> type = TypeUtils.adjustWildcards(typeView, TypeView.class);
        MethodView<Object, ?> method;
        if (function.method() == null) {
            String functionName = function.name().lexeme();
            if (arguments.isEmpty()) {
                Option<MethodView<Object, ?>> methodViewOption = type.methods().named(functionName);
                if (methodViewOption.absent()) {
                    throw new NativeExecutionException("Module Loader : Can't find function with name : " + function);
                }
                method = methodViewOption.get();
            }
            else {
                method = ExecutableLookup.method(at, type, functionName, arguments);
            }
        }
        else {
            method = TypeUtils.adjustWildcards(function.method(), MethodView.class);
        }

        if (this.supportedFunctions.stream().anyMatch(sf -> function.method().equals(method))) {
            try {
                Object result = method.invoke(this.instance(), arguments.toArray(Object[]::new)).orNull();
                return new ExternalInstance(result, TypeUtils.adjustWildcards(method.returnType(), TypeView.class));
            }
            catch(Throwable e) {
                throw new ScriptEvaluationError(e, Phase.INTERPRETING, at);
            }
        }
        else {
            throw new ScriptEvaluationError(
                    "Function '" + function.name().lexeme() + "' is not supported by module '" + this.moduleClass().getSimpleName() + "'",
                    Phase.INTERPRETING, at
            );
        }
    }

    @Override
    public List<NativeFunctionStatement> supportedFunctions(Token moduleName, Interpreter interpreter) {
        if (this.supportedFunctions == null) {
            List<NativeFunctionStatement> functionStatements = new ArrayList<>();

            TokenType identifier = interpreter.tokenRegistry().literals().identifier();
            TypeView<?> typeView = this.applicationContext().environment().introspector().introspect(this.moduleClass());
            for (MethodView<?, ?> method : typeView.methods().all()) {
                if (!method.modifiers().isPublic()) {
                    continue;
                }
                if (method.declaredBy().is(Object.class)) {
                    continue;
                }

                Token token = Token.of(identifier, method.name())
                        .virtual()
                        .build();

                List<Parameter> parameters = new ArrayList<>();
                for (ParameterView<?> parameter : method.parameters().all()) {
                    Token parameterName = Token.of(identifier)
                            .lexeme(parameter.name())
                            .virtual()
                            .build();
                    parameters.add(new Parameter(parameterName));
                }
                NativeFunctionStatement functionStatement = new NativeFunctionStatement(token, moduleName, method, parameters);
                functionStatements.add(functionStatement);
            }
            this.supportedFunctions = functionStatements;
        }
        return this.supportedFunctions;
    }
}
