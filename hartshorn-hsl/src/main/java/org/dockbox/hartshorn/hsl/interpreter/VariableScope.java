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

package org.dockbox.hartshorn.hsl.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * A variable scope represents all declared variables inside a specific scope, with access to a
 * potential enclosing scope. An example of a valid scope is inside any code block, such as an
 * if-statement's body. In this example the enclosing scope is the global scope of the script.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class VariableScope {

    private final VariableScope enclosing;
    private final Map<String, Object> valuesMap = new HashMap<>();

    public VariableScope() {
        this.enclosing = null;
    }

    public VariableScope(VariableScope enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * Gets all variable values declared inside the scope, identified by their name.
     * @return The variable values.
     */
    public Map<String, Object> values() {
        return this.valuesMap;
    }

    /**
     * Gets the variable value for the given identifier, if it exists. If the variable
     * doesn't exist in this scope, an attempt is made to look it up in the enclosing
     * scope. If the token doesn't exist in any of the accessible scopes, a
     * {@link ScriptEvaluationError} is thrown.
     *
     * @param name The identifier for the variable.
     * @return The value of the variable.
     * @throws ScriptEvaluationError If the variable is not defined.
     */
    public Object get(Token name) {
        if (this.valuesMap.containsKey(name.lexeme())) {
            return this.valuesMap.get(name.lexeme());
        }

        // If the variable isn’t found in this scope, we simply try the enclosing one
        if (this.enclosing != null) {
            return this.enclosing.get(name);
        }

        throw new ScriptEvaluationError("Undefined variable '" + name.lexeme() + "'.", Phase.INTERPRETING, name);
    }

    /**
     * Defines a new variable and assigns the given value to it. If there is already a
     * variable with the given name, the value is modified to equal the given value.
     *
     * @param name The name of the variable.
     * @param value The value to assign.
     */
    public void define(String name, Object value) {
        this.valuesMap.put(name, value);
    }

    /**
     * Reassigns the given value to an existing variable. If the variable does not exist
     * within this scope, it will be looked up in enclosing scopes. If the variable does
     * not exist in any enclosing scope, a {@link ScriptEvaluationError} is thrown.
     *
     * @param name The identifier for the variable.
     * @param value The value to assign.
     * @throws ScriptEvaluationError If the variable does not exist.
     */
    public void assign(Token name, Object value) {
        if (this.valuesMap.containsKey(name.lexeme())) {
            this.valuesMap.put(name.lexeme(), value);
            return;
        }
        // If the variable isn’t in this scope, it checks the outer one, recursively
        if (this.enclosing != null) {
            this.enclosing.assign(name, value);
            return;
        }
        throw new ScriptEvaluationError("Undefined variable '" + name.lexeme() + "'.", Phase.INTERPRETING, name);
    }

    public void assignAt(int distance, Token name, Object value) {
        this.ancestor(name, distance).valuesMap.put(name.lexeme(), value);
    }

    public boolean contains(Token token) {
        return this.contains(token.lexeme());
    }

    /**
     * Checks if this scope contains a variable with the given name. This does not attempt
     * to look up the variable in an enclosing scope.
     *
     * @param name The name of the variable.
     * @return {@code true} if the variable exists, or {@code false}.
     */
    public boolean contains(String name) {
        return this.valuesMap.containsKey(name);
    }

    /**
     * Gets the value of the variable with the given name in a scope which is a given amount
     * of steps up from the current scope. Every step up indicates the enclosing scope of
     * the previous step is selected. If there is no enclosing scope matching the amount of
     * steps, a {@link ScriptEvaluationError} is thrown.
     *
     * @param at The position from where the request is made.
     * @param distance The amount of steps up.
     * @param name The name of the variable.
     * @return The value of the variable.
     * @throws ScriptEvaluationError If there is no enclosing scope matching the amount of steps.
     */
    public Object getAt(Token at, int distance, String name) {
        return this.ancestor(at, distance).valuesMap.get(name);
    }

    /**
     * Gets the value of the variable with the given name in a scope which is a given amount
     * of steps up from the current scope. Every step up indicates the enclosing scope of
     * the previous step is selected. If there is no enclosing scope matching the amount of
     * steps, a {@link ScriptEvaluationError} is thrown.
     *
     * @param name The identifier for the variable
     * @param distance The amount of steps up.
     * @return The value of the variable.
     * @throws ScriptEvaluationError If there is no enclosing scope matching the amount of steps.
     */
    public Object getAt(Token name, int distance) {
        return this.getAt(name, distance, name.lexeme());
    }

    VariableScope ancestor(Token name, int distance) {
        VariableScope variableScope = this;
        for (int i = 0; i < distance; i++) {
            variableScope = variableScope.enclosing;
            if (variableScope == null) {
                throw new ScriptEvaluationError("No enclosing scope at distance %s for active scope.".formatted(distance), Phase.INTERPRETING, name);
            }
        }
        return variableScope;
    }

    /**
     * Gets the enclosing scope, if it exists. If no existing scope exists, {@code null}
     * is returned.
     *
     * @return The enclosing {@link VariableScope}, or {@code null}.
     */
    public VariableScope enclosing() {
        return this.enclosing;
    }
}
