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

package org.dockbox.hartshorn.hsl.objects.virtual;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.AbstractFinalizable;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.objects.external.CompositeInstance;
import org.dockbox.hartshorn.hsl.objects.external.ExternalClass;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a class definition inside a script. The class is identified by its name, and
 * can carry a variety of additional information such as the super class, methods, and an
 * optional super class.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class VirtualClass extends AbstractFinalizable implements ClassReference {

    private final String name;
    private final ClassReference superClass;
    private final VirtualFunction constructor;
    private final VariableScope variableScope;
    private final Map<String, VirtualFunction> methods;
    private final Map<String, VirtualProperty> fields;
    private final boolean isDynamic;

    public VirtualClass(final String name,
                        final ClassReference superClass,
                        final VirtualFunction constructor,
                        final VariableScope variableScope,
                        final Map<String, VirtualFunction> methods,
                        final Map<String, VirtualProperty> fields,
                        final boolean finalized,
                        final boolean isDynamic
    ) {
        super(finalized);
        this.name = name;
        this.superClass = superClass;
        this.constructor = constructor;
        this.variableScope = variableScope;
        this.methods = methods;
        this.fields = fields;
        this.isDynamic = isDynamic;
    }

    /**
     * Gets the name of the class.
     *
     * @return The name of the class.
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the super class of the class.
     *
     * @return The super class of the class.
     */
    @Nullable
    @Override
    public ClassReference superClass() {
        return this.superClass;
    }

    /**
     * Gets all methods of the class.
     *
     * @return All methods of the class.
     */
    public Map<String, VirtualFunction> methods() {
        return this.methods;
    }

    /**
     * Gets all fields of the class.
     *
     * @return All fields of the class.
     */
    public Map<String, VirtualProperty> fields() {
        return this.fields;
    }

    public VirtualProperty property(final String name) {
        return this.fields.get(name);
    }

    /**
     * Adds a method to the class.
     *
     * @param name
     *         The name of the method.
     * @param function
     *         The function to add.
     */
    public void extensionMethod(final String name, final VirtualFunction function) {
        this.methods.put(name, function);
    }

    /**
     * Looks up a method by name. If no method is found, {@code null} is returned.
     *
     * @param name
     *         The name of the method.
     *
     * @return The method, or {@code null} if no method is found.
     */
    @Override
    public MethodReference method(final String name) {
        if (this.methods.containsKey(name)) {
            return this.methods.get(name);
        }
        // If we can't find this method in class check if this method is from super class
        if (this.superClass != null) {
            return this.superClass.method(name);
        }
        return null;
    }

    /**
     * Gets the variable scope of the class.
     *
     * @return The variable scope of the class.
     */
    public VariableScope variableScope() {
        return this.variableScope;
    }

    public boolean isDynamic() {
        return this.isDynamic;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final InstanceReference instance, final List<Object> arguments) throws ApplicationException {
        if (instance != null) {
            throw new ScriptEvaluationError("Cannot call a class as an instance", Phase.INTERPRETING, at);
        }
        if (this.superClass instanceof ExternalClass) {
            final CompositeInstance compositeInstance = new CompositeInstance(this);
            compositeInstance.makeInstance(at, interpreter, arguments, this.constructor());
            return compositeInstance;
        }
        else {
            final InstanceReference virtualInstance = new VirtualInstance(this);
            // Acts as a virtual constructor
            final VirtualFunction initializer = this.constructor();
            if (initializer != null) {
                initializer.bind(virtualInstance).call(at, interpreter, virtualInstance, arguments);
            }
            return virtualInstance;
        }
    }

    @Override
    public VirtualFunction constructor() {
        return this.constructor;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        final var that = (VirtualClass) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.superClass, that.superClass) &&
                Objects.equals(this.constructor, that.constructor) &&
                Objects.equals(this.variableScope, that.variableScope) &&
                Objects.equals(this.methods, that.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.superClass, this.constructor, this.variableScope, this.methods);
    }

}
