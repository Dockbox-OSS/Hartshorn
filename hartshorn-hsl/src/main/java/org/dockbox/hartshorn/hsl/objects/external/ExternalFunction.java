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

package org.dockbox.hartshorn.hsl.objects.external;

import java.util.List;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.AbstractFinalizable;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.objects.NativeExecutionException;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents one or more Java methods that can be called from an HSL runtime. The methods
 * are identified by their name, but without the return type or the parameter types. The
 * exact method is determined by the arguments that are passed to the method. If no matching
 * method is found, a {@link ScriptEvaluationError} is thrown.
 *
 * @author Guus Lieben
 * @since 0.4.12
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
        if (methods.isEmpty()) {
            throw new ScriptEvaluationError(
                    "Method '" + this.methodName + "' with " + arguments.size() + " parameters does not exist on external instance of type " + this.type.name(),
                    Phase.INTERPRETING, at
            );
        }

        MethodView<Object, ?> executable = ExecutableLookup.executable(methods, arguments);
        if (executable != null) {
            return executable;
        }

        throw new ScriptEvaluationError(
                "Method '" + this.methodName + "' with parameters accepting " + arguments + " does not exist on external instance of type " + this.type.name(),
                Phase.INTERPRETING, at
        );
    }

    @Override
    public Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments) throws ApplicationException {
        if (this.instance != null && instance != this.instance) {
            throw new ScriptEvaluationError(
                    "Function reference was bound to " + this.instance + ", but was invoked with a different object " + instance,
                    Phase.INTERPRETING, at
            );
        }
        if (!(instance instanceof ExternalObjectReference externalObjectReference)) {
            throw new ScriptEvaluationError(
                    "Cannot call method '" + this.methodName + "' on non-external instance",
                    Phase.INTERPRETING, at
            );
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
            throw new NativeExecutionException("Failed to call method " + this.methodName + " on external instance of type " + this.type.name(), throwable);
        }
    }

    @Override
    public String toString() {
        return this.type.qualifiedName() + "#" + this.methodName;
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
            throw new ScriptEvaluationError(
                    "Cannot bind external function to virtual instance of type " + virtualClass.name(),
                    Phase.INTERPRETING, -1, -1
                    );
        }

        return new ExternalFunction(externalClass.type(), this.methodName, instance);
    }

    @Override
    public InstanceReference bound() {
        return this.instance;
    }
}
