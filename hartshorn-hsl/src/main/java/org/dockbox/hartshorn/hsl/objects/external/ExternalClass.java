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
import org.dockbox.hartshorn.hsl.interpreter.ExternalClassRegistry;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.Map;

/**
 * Represents a Java class that can be called from an HSL runtime. This class can be
 * used to create a new instance of the class. This requires the class to be imported
 * by the responsible {@link ScriptRuntime} through {@link ScriptRuntime#imports(Map)}.
 *
 * <pre>{@code
 * AbstractHslRuntime runtime = ...;
 * runtime.imports(Map.of("MyClass", MyClass.class));
 * runtime.run("var instance = MyClass();");
 * }</pre>
 *
 * @param registry The registry that manages this class.
 * @param type The type represented by this reference.
 * @param alias The alias under which this class was imported.
 * @param <T> The type of the class.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public record ExternalClass<T>(ExternalClassRegistry registry, TypeView<T> type, String alias) implements ClassReference {

    @Override
    public Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments) throws ApplicationException {
        if (instance != null) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.CONSTRUCTOR_CALL_ON_INSTANCE)
                    .at(at)
                    .build();
        }
        ConstructorView<T> executable = ExecutableLookup.executable(this.type.constructors().all(), arguments);
        if (executable != null) {
            try {
                T objectInstance = executable.create(arguments.toArray());
                return new ExternalInstance(objectInstance, this);
            }
            catch (ApplicationException e) {
                throw e;
            }
            catch (Throwable throwable) {
                throw new ApplicationException(throwable);
            }
        }
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.MISSING_CONSTRUCTOR_WITH_PARAMETERS, this.type.name(), arguments)
                .at(at)
                .build();
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("type", this.type.qualifiedName())
                .describe();
    }

    @Override
    public VirtualFunction constructor() {
        return null;
    }

    @Override
    public MethodReference method(String name) {
        return new ExternalFunction(this, name);
    }

    @Override
    public ClassReference superClass() {
        TypeView<?> parent = this.type().superClass();
        if (parent.isVoid()) {
            return null;
        }
        return this.registry().defineClass(parent);
    }

    @Override
    public String name() {
        return this.alias();
    }

    @Override
    public boolean isFinal() {
        return this.type().modifiers().isFinal();
    }

    @Override
    public void makeFinal() {
        throw new UnsupportedOperationException("Cannot change modifiers of external class");
    }
}
