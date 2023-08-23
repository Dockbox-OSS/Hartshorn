/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.NativeExecutionException;
import org.dockbox.hartshorn.hsl.objects.external.ExecutableLookup;
import org.dockbox.hartshorn.hsl.objects.external.ExternalInstance;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one or more Java methods that can be called from an HSL runtime. The methods
 * are pre-filtered based on their access-level. The methods are provided as {@link NativeFunctionStatement}s.
 *
 * <p>Methods can be executed based on the supported functions of the module. If the method
 * is not known at compile-time, it is looked up at run-time. If the method is not found, is
 * not accessible, or if it is not supported by the module, a {@link RuntimeError} is thrown.
 *
 * <p>If the method is not accessible, or any cannot be invoked, a {@link NativeExecutionException}
 * is thrown. For all other errors, a {@link RuntimeError} is thrown.
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
    public Object call(final Token at, final Interpreter interpreter, final NativeFunctionStatement function, final List<Object> arguments) throws NativeExecutionException {
        try {
            final TypeView<Object> type = TypeUtils.adjustWildcards(this.applicationContext().environment().introspect(this.moduleClass()), TypeView.class);
            final MethodView<Object, ?> method;
            if (function.method() == null) {
                final String functionName = function.name().lexeme();
                if (arguments.isEmpty()) {
                    final Option<MethodView<Object, ?>> methodViewOption = type.methods().named(functionName);
                    if (methodViewOption.absent())
                        throw new NativeExecutionException("Module Loader : Can't find function with name : " + function);

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
                final Object result = method.invoke(this.instance(), arguments.toArray(Object[]::new))
                        .mapError(ApplicationException::new)
                        .orNull();

                return new ExternalInstance(result, TypeUtils.adjustWildcards(method.returnType(), TypeView.class));
            } else throw new RuntimeError(at, "Function '" + function.name().lexeme() + "' is not supported by module '" + this.moduleClass().getSimpleName() + "'");
        }
        catch (final Throwable e) {
            throw new RuntimeError(at, e.getMessage());
        }
    }

    @Override
    public List<NativeFunctionStatement> supportedFunctions(final Token moduleName) {
        if (this.supportedFunctions == null) {
            final List<NativeFunctionStatement> functionStatements = new ArrayList<>();

            final TypeView<?> typeView = this.applicationContext().environment().introspect(this.moduleClass());
            for (final MethodView<?, ?> method : typeView.methods().all()) {
                if (!method.modifiers().isPublic()) continue;
                if (method.declaredBy().is(Object.class)) continue;

                final Token token = new Token(TokenType.IDENTIFIER, method.name(), -1, -1);

                final List<Parameter> parameters = new ArrayList<>();
                for (final ParameterView<?> parameter : method.parameters().all()) {
                    final Token parameterName = new Token(TokenType.IDENTIFIER, parameter.name(), -1, -1);
                    parameters.add(new Parameter(parameterName));
                }
                final NativeFunctionStatement functionStatement = new NativeFunctionStatement(token, moduleName, method, parameters);
                functionStatements.add(functionStatement);
            }
            this.supportedFunctions = functionStatements;
        }
        return this.supportedFunctions;
    }
}
