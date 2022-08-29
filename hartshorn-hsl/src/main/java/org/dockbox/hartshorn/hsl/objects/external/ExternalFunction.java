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

package org.dockbox.hartshorn.hsl.objects.external;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.AbstractFinalizable;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
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
public class ExternalFunction extends AbstractFinalizable implements MethodReference {

    private final String methodName;
    private final TypeContext<Object> type;
    private final InstanceReference instance;

    public ExternalFunction(final Class<?> type, final String methodName) {
        this(type, methodName, null);
    }

    public ExternalFunction(final Class<?> type, final String methodName, final InstanceReference instance) {
        super(false);
        this.methodName = methodName;
        this.type = (TypeContext<Object>) TypeContext.of(type);
        this.instance = instance;
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
    public TypeContext<?> type() {
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
            throw new RuntimeError(at, DiagnosticMessage.MISSING_METHOD_WITH_COUNT, this.methodName, arguments.size(), this.type.name());
        }

        final MethodContext<?, Object> executable = ExecutableLookup.executable(methods, arguments);
        if (executable != null) return executable;

        throw new RuntimeError(at, DiagnosticMessage.MISSING_METHOD_WITH_PARAMETERS, this.methodName, arguments, this.type.name());
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final InstanceReference instance, final List<Object> arguments) throws ApplicationException {
        if (this.instance != null && instance != this.instance) {
            throw new RuntimeError(at, DiagnosticMessage.ILLEGAL_METHOD_BINDING_CALL, this.instance, instance);
        }
        if (!(instance instanceof ExternalObjectReference externalObjectReference)) {
            throw new RuntimeError(at, DiagnosticMessage.NON_EXTERNAL_OBJECT_CALL, this.methodName);
        }
        final MethodContext<?, Object> method = this.method(at, arguments);
        final Result<?> result = method.invoke(externalObjectReference.externalObject(), arguments);
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

    @Override
    public MethodReference bind(final InstanceReference instance) {
        final ClassReference virtualClass = instance.type();
        ClassReference classReference = virtualClass;
        ExternalClass<?> externalClass = null;

        while (externalClass == null && classReference != null) {
            if (classReference instanceof ExternalClass<?> external) {
                externalClass = external;
                break;
            }
            classReference = classReference.superClass();
        }

        if (externalClass == null) {
            throw new RuntimeError(null, DiagnosticMessage.ILLEGAL_EXTERNAL_FUNCTION_BINDING, virtualClass.name());
        }

        return new ExternalFunction(externalClass.type().type(), this.methodName, instance);
    }

    @Override
    public InstanceReference bound() {
        return this.instance;
    }
}
