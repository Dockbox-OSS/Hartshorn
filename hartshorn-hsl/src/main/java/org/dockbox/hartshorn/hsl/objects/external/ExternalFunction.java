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

package org.dockbox.hartshorn.hsl.objects.external;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.AbstractFinalizable;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

/**
 * Represents one or more Java methods that can be called from an HSL runtime. The methods
 * are identified by their name, but without the return type or the parameter types. The
 * exact method is determined by the arguments that are passed to the method. If no matching
 * method is found, a {@link ScriptEvaluationError} is thrown.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ExternalFunction extends AbstractFinalizable implements MethodReference {

    private final String methodName;
    private final TypeView<Object> type;
    private final InstanceReference instance;

    public ExternalFunction(TypeView<?> type, String methodName) {
        this(type, methodName, null);
    }

    private ExternalFunction(TypeView<?> type, String methodName, InstanceReference instance) {
        super(false);
        this.methodName = methodName;
        this.type = (TypeView<Object>) type;
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
     * Returns the {@link TypeView} which declares the method represented by this class.
     * @return The {@link TypeView} which declares the method represented by this class.
     */
    public TypeView<?> type() {
        return this.type;
    }

    private MethodView<Object, ?> method(Token at, List<Object> arguments) {
        Option<MethodView<Object, ?>> zeroParameterMethod = this.type.methods().named(this.methodName);
        if (arguments.isEmpty() && zeroParameterMethod.present()) {
            return zeroParameterMethod.get();
        }
        List<MethodView<Object, ?>> methods = this.type.methods().all().stream()
                .filter(method -> method.name().equals(this.methodName))
                .filter(method -> method.parameters().count() == arguments.size())
                .toList();

        MethodView<Object, ?> executable = ExecutableLookup.executable(methods, arguments);
        if (executable != null) {
            return executable;
        }
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.MISSING_METHOD_WITH_PARAMETERS, this.methodName, arguments, this.type.name())
                .at(at)
                .build();
    }

    @Override
    public Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments) throws ApplicationException {
        if (this.instance != null && instance != this.instance) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.ILLEGAL_METHOD_BINDING_CALL, this.instance, instance)
                    .at(at)
                    .build();
        }
        if (!(instance instanceof ExternalObjectReference externalObjectReference)) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.NON_EXTERNAL_OBJECT_CALL, this.methodName)
                    .at(at)
                    .build();
        }
        MethodView<Object, ?> method = this.method(at, arguments);

        try {
            return method.invoke(externalObjectReference.externalObject(), arguments)
                    .map(object -> new ExternalInstance(object,
                            interpreter.applicationContext().environment().introspector().introspect(object)))
                    .orNull();
        }
        catch (ApplicationException e) {
            throw e;
        }
        catch (Throwable throwable) {
            throw new ApplicationException(throwable);
        }
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("type", this.type.qualifiedName())
                .field("methodName", this.methodName)
                .field("instance", this.instance)
                .describe();
    }

    @Override
    public MethodReference bind(InstanceReference instance) {
        ClassReference virtualClass = instance.type();
        ClassReference classReference = virtualClass;
        ExternalClass<?> externalClass = null;

        do {
            if(classReference instanceof ExternalClass<?> external) {
                externalClass = external;
                break;
            }
            classReference = classReference.superClass();
        }
        while(classReference != null);

        if (externalClass == null) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.ILLEGAL_EXTERNAL_FUNCTION_BINDING, virtualClass.name())
                    .position(-1, -1)
                    .build();
        }

        return new ExternalFunction(externalClass.type(), this.methodName, instance);
    }

    @Override
    public InstanceReference bound() {
        return this.instance;
    }
}
