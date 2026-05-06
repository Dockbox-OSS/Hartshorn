/*
 * Copyright 2019-2026 the original author or authors.
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
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.access.StandardPropertyAccessVerifier;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClass;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualProperty;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * A {@link VirtualInstance} that combines the capabilities of a virtual instance and an external
 * instance. This is typically used when a virtual class extends an external class. Any fields and
 * functions defined in the external instance are directly modified on the external instance, while
 * fields and functions defined in the virtual instance are handled by the virtual instance.
 *
 * @param <T> the type of the first external class in the inheritance hierarchy
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class CompositeInstance<T> extends VirtualInstance implements ExternalObjectReference {

    private final TypeView<T> firstExternalClass;
    private T instance;

    public CompositeInstance(@NonNull VirtualClass virtualClass) {
        super(virtualClass);
        ClassReference superClass = virtualClass.superClass();
        while (superClass != null && !(superClass instanceof ExternalClass<?>)) {
            superClass = superClass.superClass();
        }
        if (superClass == null) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.COMPOSITE_WITHOUT_EXTERNAL_SUPER, virtualClass.name())
                .at(virtualClass.constructor().declaration())
                .build();
        }
        this.firstExternalClass =
            TypeUtils.unchecked(((ExternalClass<?>) superClass).type(), TypeView.class);
        if (this.firstExternalClass.constructors().defaultConstructor().absent()) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.MISSING_DEFAULT_CONSTRUCTOR, firstExternalClass.name())
                .at(virtualClass.constructor().declaration())
                .build();
        }
    }

    /**
     * Creates the external instance and calls the virtual constructor, if any.
     *
     * @param at the token representing the position of the constructor call
     * @param interpreter the interpreter executing the constructor
     * @param arguments the arguments to pass to the virtual constructor
     * @param virtualConstructor the virtual constructor to call, or {@code null} if none
     *
     * @throws ApplicationException if an error occurs during instance creation or virtual
     * constructor execution
     */
    public void makeInstance(
        Token at,
        Interpreter interpreter,
        List<Object> arguments,
        VirtualFunction virtualConstructor
    ) throws ApplicationException {
        if (this.instance != null) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.BACKING_INSTANCE_ALREADY_CREATED)
                .at(at)
                .build();
        }
        // External class constructor
        ConstructorView<T> constructor =
            this.firstExternalClass.constructors().defaultConstructor().get();
        try {
            this.instance = constructor.create();
        }
        catch (ApplicationException e) {
            throw e;
        }
        catch (Throwable throwable) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.UNEXPECTED_ERROR, throwable.getMessage())
                .at(at)
                .cause(throwable)
                .build();
        }

        // Virtual class constructor
        if (virtualConstructor != null) {
            virtualConstructor.call(at, interpreter, this, arguments);
        }
    }

    @Override
    public void set(
        final Interpreter interpreter,
        final Token name,
        final Object value,
        final VariableScope fromScope
    ) {
        this.checkInstance(name);
        final VirtualProperty property = super.type().property(name.lexeme());
        if (property != null) {
            super.set(interpreter, name, value, fromScope);
        }
        else {
            Option<FieldView<T, ?>> field = this.firstExternalClass.fields().named(name.lexeme());
            if (field.present()) {
                try {
                    field.get().set(this.instance, value);
                }
                catch (Throwable throwable) {
                    throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                        .at(name)
                        .message(DiagnosticMessage.PROPERTY_ACCESS_FAILURE,
                            StandardPropertyAccessVerifier.WRITE_ACTION,
                            name.lexeme(),
                            this.typeName(),
                            throwable.getMessage()
                        )
                        .cause(throwable)
                        .build();
                }
            }
            else {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .at(name)
                    .message(DiagnosticMessage.UNDEFINED_PROPERTY,
                        name.lexeme(),
                        this.typeName())
                    .build();
            }
        }
    }

    @Override
    public Object get(
        final Interpreter interpreter,
        final Token name,
        final VariableScope fromScope
    ) {
        this.checkInstance(name);
        final VirtualProperty property = super.type().property(name.lexeme());
        if (property != null) {
            return super.get(interpreter, name, fromScope);
        }
        else {
            Option<FieldView<T, ?>> field = this.firstExternalClass.fields().named(name.lexeme());
            if (field.present()) {
                try {
                    return field.get().get(this.instance);
                }
                catch (Throwable throwable) {
                    throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                        .at(name)
                        .message(DiagnosticMessage.PROPERTY_ACCESS_FAILURE,
                            StandardPropertyAccessVerifier.READ_ACTION,
                            name.lexeme(),
                            this.typeName(),
                            throwable.getMessage()
                        )
                        .cause(throwable)
                        .build();
                }
            }
            else {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .at(name)
                    .message(DiagnosticMessage.UNDEFINED_PROPERTY,
                        name.lexeme(),
                        this.typeName())
                    .build();
            }
        }
    }

    @Override
    public @Nullable Object externalObject() {
        return this.instance;
    }

    private void checkInstance(Token position) {
        if (this.instance == null) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .at(position)
                .message(DiagnosticMessage.DEFERRED_INSTANCE_EAGER_ACCESS,
                    this.typeName())
                .build();
        }
    }

    private String typeName() {
        return this.firstExternalClass.name();
    }
}
