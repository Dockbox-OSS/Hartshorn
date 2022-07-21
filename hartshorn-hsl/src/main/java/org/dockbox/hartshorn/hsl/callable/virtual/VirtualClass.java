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

package org.dockbox.hartshorn.hsl.callable.virtual;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.hsl.callable.CallableNode;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;
import java.util.Map;

/**
 * Represents a class definition inside a script. The class is identified by its name, and
 * can carry a variety of additional information such as the superclass, methods, and an
 * optional superclass.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class VirtualClass implements CallableNode {

    private final String name;
    private final VirtualClass superClass;
    private final VariableScope variableScope;
    private final Map<String, VirtualFunction> methods;

    public VirtualClass(final String name, final VirtualClass superClass, final VariableScope variableScope, final Map<String, VirtualFunction> methods) {
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
        this.variableScope = variableScope;
    }

    /**
     * Gets the name of the class.
     * @return The name of the class.
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the superclass of the class.
     * @return The superclass of the class.
     */
    @Nullable
    public VirtualClass superClass() {
        return this.superClass;
    }

    /**
     * Gets all methods of the class.
     * @return All methods of the class.
     */
    public Map<String, VirtualFunction> methods() {
        return this.methods;
    }

    /**
     * Adds a method to the class.
     * @param name The name of the method.
     * @param function The function to add.
     */
    public void addMethod(final String name, final VirtualFunction function) {
        this.methods.put(name, function);
    }

    /**
     * Looks up a method by name. If no method is found, {@code null} is returned.
     * @param name The name of the method.
     * @return The method, or {@code null} if no method is found.
     */
    public VirtualFunction findMethod(final String name) {
        if (this.methods.containsKey(name)) {
            return this.methods.get(name);
        }
        // If we can't find this method in class check if this method is from super class
        if (this.superClass != null) {
            return this.superClass.findMethod(name);
        }
        return null;
    }

    /**
     * Gets the variable scope of the class.
     * @return The variable scope of the class.
     */
    public VariableScope variableScope() {
        return this.variableScope;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final List<Object> arguments) {
        final VirtualInstance instance = new VirtualInstance(this);
        // Acts as a virtual constructor
        final VirtualFunction initializer = this.findMethod(VirtualFunction.CLASS_INIT);
        if (initializer != null) {
            initializer.bind(instance).call(at, interpreter, arguments);
        }
        return instance;
    }
}
