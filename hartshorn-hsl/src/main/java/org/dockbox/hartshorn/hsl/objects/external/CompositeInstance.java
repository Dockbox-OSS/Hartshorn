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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClass;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1061 Add documentation
 *
 * @param <T>> ...
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
            throw new IllegalArgumentException("No external class found in " + virtualClass.name());
        }
        this.firstExternalClass = TypeUtils.adjustWildcards(((ExternalClass<?>) superClass).type(), TypeView.class);
        if (this.firstExternalClass.constructors().defaultConstructor().absent()) {
            throw new IllegalArgumentException("No empty or default constructor found in " + this.firstExternalClass.name() + ", composite instances cannot carry complex constructors.");
        }
    }

    public void makeInstance(Token at, Interpreter interpreter, List<Object> arguments, VirtualFunction virtualConstructor) throws ApplicationException {
        if (this.instance != null) {
            throw new IllegalStateException("Instance already made");
        }
        // External class constructor
        ConstructorView<T> constructor = this.firstExternalClass.constructors().defaultConstructor().get();
        try {
            this.instance = constructor.create().orNull();
        }
        catch (ApplicationException e) {
            throw e;
        }
        catch (Throwable throwable) {
            throw new ApplicationException(throwable);
        }

        // Virtual class constructor
        if (virtualConstructor != null) {
            virtualConstructor.call(at, interpreter, this, arguments);
        }
    }

    @Override
    public void set(Token name, Object value, VariableScope fromScope, ExecutionOptions options) {
        this.checkInstance();
        FieldStatement virtualField = super.type().field(name.lexeme());
        if (virtualField != null) {
            super.set(name, value, fromScope, options);
        }
        else {
            Option<FieldView<T, ?>> field = this.firstExternalClass.fields().named(name.lexeme());
            if (field.present()) {
                try {
                    field.get().set(this.instance, value);
                }
                catch (Throwable throwable) {
                    throw new ScriptEvaluationError(
                            throwable, "Failed to set property %s on external instance of type %s".formatted(name.lexeme(), this.firstExternalClass.name()),
                            Phase.INTERPRETING, name
                    );
                }
            }
            else {
                throw new IllegalArgumentException("Field " + name.lexeme() + " not found in " + this.firstExternalClass.name());
            }
        }
    }

    @Override
    public Object get(Token name, VariableScope fromScope, ExecutionOptions options) {
        this.checkInstance();
        FieldStatement virtualField = super.type().field(name.lexeme());
        if (virtualField != null) {
            return super.get(name, fromScope, options);
        }
        else {
            Option<FieldView<T, ?>> field = this.firstExternalClass.fields().named(name.lexeme());
            if (field.present()) {
                try {
                    return field.get().get(this.instance);
                }
                catch (Throwable throwable) {
                    throw new ScriptEvaluationError(
                            throwable, "Failed to get property %s from external instance of type %s".formatted(name.lexeme(), this.firstExternalClass.name()),
                            Phase.INTERPRETING, name
                    );
                }
            }
            else {
                throw new IllegalArgumentException("Field " + name.lexeme() + " not found in " + this.firstExternalClass.name());
            }
        }
    }

    @Override
    public @Nullable Object externalObject() {
        this.checkInstance();
        return this.instance;
    }

    private void checkInstance() {
        if (this.instance == null) {
            throw new IllegalStateException("Instance not created");
        }
    }
}
