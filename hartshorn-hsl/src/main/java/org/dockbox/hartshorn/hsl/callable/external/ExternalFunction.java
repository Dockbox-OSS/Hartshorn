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

package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.callable.CallableNode;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.List;

/**
 * Represents one or more Java methods that can be called from an HSL runtime. The methods
 * are identified by their name, but without the return type or the parameter types. The
 * exact method is determined by the arguments that are passed to the method. If no matching
 * method is found, a {@link RuntimeError} is thrown.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class ExternalFunction implements CallableNode {

    private final Object instance;
    private final String methodName;
    private final TypeContext<Object> type;

    public ExternalFunction(final Object instance, final String methodName) {
        this.instance = instance;
        this.methodName = methodName;
        this.type = TypeContext.of(instance);
    }

    /**
     * Returns the instance which is used to call the methods.
     * @return The instance which is used to call the methods.
     */
    public Object instance() {
        return this.instance;
    }

    /**
     * Returns the name of the method.
     * @return The name of the method.
     */
    public String methodName() {
        return this.methodName;
    }

    /**
     * Returns the {@link TypeContext} which declares the method represented by this class.
     * @return The {@link TypeContext} which declares the method represented by this class.
     */
    public TypeContext<Object> type() {
        return this.type;
    }

    private MethodContext<?, Object> method(final Token at, final List<Object> arguments) {
        final Result<MethodContext<?, Object>> zeroParameterMethod = this.type.method(this.methodName);
        if (arguments.isEmpty() && zeroParameterMethod.present()) {
            return zeroParameterMethod.get();
        }
        final List<MethodContext<?, Object>> methods = this.type.methods().stream()
                .filter(m -> m.name().equals(this.methodName))
                .filter(m -> m.parameterCount() == arguments.size())
                .toList();
        if (methods.isEmpty()) {
            throw new RuntimeError(at, "Method '" + this.methodName + "' with " + arguments.size() + " parameters does not exist on external instance of type " + this.type.name());
        }

        final MethodContext<?, Object> executable = ExecutableLookup.executable(methods, arguments);
        if (executable != null) return executable;

        throw new RuntimeError(at, "Method '" + this.methodName + "' with parameters accepting " + arguments + " does not exist on external instance of type " + this.type.name());
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final List<Object> arguments) throws ApplicationException {
        final MethodContext<?, Object> method = this.method(at, arguments);
        final Result<?> result = method.invoke(this.instance, arguments);
        if (result.caught()) {
            if (result.error() instanceof ApplicationException ae) throw ae;
            throw new ApplicationException(result.error());
        }
        return result.map(ExternalInstance::new).orNull();
    }

    @Override
    public String toString() {
        return this.type.qualifiedName() + "#" + this.methodName;
    }
}
