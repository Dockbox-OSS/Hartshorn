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

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClass;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.List;

public class CompositeInstance extends VirtualInstance implements ExternalObjectReference {

    private final TypeContext<?> firstExternalClass;
    private Object instance;

    public CompositeInstance(final VirtualClass virtualClass) {
        super(virtualClass);
        ClassReference superClass = virtualClass.superClass();
        while (superClass != null && !(superClass instanceof ExternalClass<?>)) {
            superClass = superClass.superClass();
        }
        if (superClass == null) {
            throw new IllegalArgumentException("No external class found in " + virtualClass.name());
        }
        this.firstExternalClass = ((ExternalClass<?>) superClass).type();
        if (this.firstExternalClass.constructor().absent()) {
            throw new IllegalArgumentException("No empty or default constructor found in " + this.firstExternalClass.name() + ", composite instances cannot carry complex constructors.");
        }
    }

    public void makeInstance(final Token at, final Interpreter interpreter, final List<Object> arguments, final VirtualFunction virtualConstructor) {
        if (this.instance != null) {
            throw new IllegalStateException("Instance already made");
        }
        // External class constructor
        final ConstructorContext<?> constructor = this.firstExternalClass.constructor().get();
        this.instance = constructor.createInstance().rethrowUnchecked().orNull();
        // Virtual class constructor
        if (virtualConstructor != null) {
            virtualConstructor.call(at, interpreter, this, arguments);
        }
    }

    @Override
    public void set(final Token name, final Object value, final VariableScope fromScope) {
        this.checkInstance();
        final FieldStatement virtualField = super.type().field(name.lexeme());
        if (virtualField != null) {
            super.set(name, value, fromScope);
        }
        else {
            final Result<FieldContext<?>> field = this.firstExternalClass.field(name.lexeme());
            if (field.present()) {
                field.get().set(this.instance, value);
            }
            else {
                throw new IllegalArgumentException("Field " + name.lexeme() + " not found in " + this.firstExternalClass.name());
            }
        }
    }

    @Override
    public Object get(final Token name, final VariableScope fromScope) {
        this.checkInstance();
        final FieldStatement virtualField = super.type().field(name.lexeme());
        if (virtualField != null) {
            return super.get(name, fromScope);
        }
        else {
            final Result<FieldContext<?>> field = this.firstExternalClass.field(name.lexeme());
            if (field.present()) {
                return field.get().get(this.instance);
            }
            else {
                throw new IllegalArgumentException("Field " + name.lexeme() + " not found in " + this.firstExternalClass.name());
            }
        }
    }

    @Override
    public Object externalObject() {
        this.checkInstance();
        return this.instance;
    }

    private void checkInstance() {
        if (this.instance == null) {
            throw new IllegalStateException("Instance not created");
        }
    }
}
