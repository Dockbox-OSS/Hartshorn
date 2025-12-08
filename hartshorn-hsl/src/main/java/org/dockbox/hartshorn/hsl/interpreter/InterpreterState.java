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

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.objects.external.ExternalClass;
import org.dockbox.hartshorn.hsl.objects.external.ExternalInstance;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The state of an {@link Interpreter}, holding variable scopes, resolved locals, and external
 * modules and variables. This state is mutable and can be reset when the interpreter is reused.
 *
 * <p>Due to its mutability, care must be taken when sharing state instances across multiple
 * threads. In principle,
 * all values tracked by this state are stored in thread-safe collections, but the variable scopes
 * themselves are not inherently thread-safe.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class InterpreterState {

    private final Map<Expression, Integer> locals = new ConcurrentHashMap<>();

    private final Map<String, ExternalInstance> externalVariables = new ConcurrentHashMap<>();
    private final Map<String, NativeModule> externalModules = new ConcurrentHashMap<>();
    private final ExternalClassRegistry externalClassRegistry = new SimpleExternalClassRegistry();

    private final Interpreter owner;

    private VariableScope global = new VariableScope();
    private VariableScope visitingScope = this.global;

    public InterpreterState(Interpreter owner) {
        this.owner = owner;
    }

    /**
     * Gets the global variable scope. This is the highest-level scope that contains all global
     * variables.
     *
     * @return the global variable scope
     *
     * @see Interpreter#global()
     */
    public VariableScope global() {
        return this.global;
    }

    /**
     * Gets the currently visiting variable scope. This is the scope that is currently being
     * executed by the interpreter.
     *
     * @return the currently visiting variable scope
     *
     * @see Interpreter#visitingScope()
     */
    public VariableScope visitingScope() {
        return this.visitingScope;
    }

    /**
     * Restores the interpreter state to its initial condition, clearing all local variable
     * resolutions and resetting the global and visiting scopes. External variables and modules are
     * preserved.
     *
     * @see Interpreter#restore()
     */
    public void restore() {
        this.global = new VariableScope();
        this.visitingScope = this.global;
        this.locals.clear();
    }

    /**
     * Registers an external module under the given name.
     *
     * @param name the name of the module
     * @param module the external module instance
     */
    public void externalModule(String name, NativeModule module) {
        this.externalModules.put(name, module);
    }

    /**
     * Registers multiple external modules.
     *
     * @param externalModules a map of module names to external module instances
     */
    public void externalModules(Map<String, NativeModule> externalModules) {
        this.externalModules.putAll(externalModules);
    }

    /**
     * Gets the registered external modules.
     *
     * @return a map of module names to external module instances
     */
    public Map<String, NativeModule> externalModules() {
        return Map.copyOf(this.externalModules);
    }

    /**
     * Registers global (external) variables that can be accessed from the interpreter.
     *
     * @param globalVariables a map of variable names to their instances
     */
    public void global(Map<String, Object> globalVariables) {
        globalVariables.forEach((name, instance) -> {
            TypeView<Object> typeView =
                this.owner.applicationContext().environment().introspector().introspect(instance);
            this.externalVariables.put(name,
                new ExternalInstance(instance, this.externalClassRegistry.defineClass(typeView)));
        });
    }

    /**
     * Gets the registry of external classes.
     *
     * @return the external class registry
     */
    public ExternalClassRegistry externalClassRegistry() {
        return this.externalClassRegistry;
    }

    /**
     * Enters the given variable scope, making it the currently visiting scope. The previous scope
     * is not preserved unless manually stored beforehand.
     *
     * @param scope the variable scope to enter
     *
     * @see Interpreter#enterScope(VariableScope)
     */
    public void enterScope(VariableScope scope) {
        this.visitingScope = scope;
    }

    /**
     * Executes the given runnable within the context of the specified variable scope. The previous
     * scope is restored after the runnable has been executed.
     *
     * @param scope the variable scope to enter
     * @param runnable the runnable to execute within the scope
     */
    public void withScope(VariableScope scope, Runnable runnable) {
        VariableScope previous = this.visitingScope();
        try {
            this.enterScope(scope);
            runnable.run();
        }
        finally {
            this.enterScope(previous);
        }
    }

    /**
     * Executes the given runnable within a new child scope of the currently visiting scope. The
     * previous scope is restored after the runnable has been executed.
     *
     * @param runnable the runnable to execute within the new child scope
     *
     * @see Interpreter#withNextScope(Runnable)
     */
    public void withNextScope(Runnable runnable) {
        VariableScope nextScope = new VariableScope(this.visitingScope());
        this.withScope(nextScope, runnable);
    }

    /**
     * Gets the distance of the given expression from the current scope.
     *
     * @param expression the expression to get the distance for
     *
     * @return the distance of the expression, or {@code null} if not resolved
     *
     * @see Interpreter#distance(Expression)
     */
    public Integer distance(Expression expression) {
        return this.locals.get(expression);
    }

    /**
     * Resolves the given expression to the specified depth in the scope chain.
     *
     * @param expression the expression to resolve
     * @param depth the depth in the scope chain
     *
     * @see Interpreter#resolve(Expression, int)
     */
    public void resolve(Expression expression, int depth) {
        this.locals.put(expression, depth);
    }

    /**
     * Looks up the value of a variable by its name and associated expression.
     *
     * @param name the token representing the variable name
     * @param expression the expression associated with the variable
     *
     * @return the value of the variable
     *
     * @see Interpreter#lookUpVariable(Token, Expression)
     */
    public Object lookUpVariable(Token name, Expression expression) {
        if (name.type() == ObjectTokenType.THIS) {
            return this.visitingScope().getAt(name, 1);
        }

        Integer distance = this.locals.get(expression);
        if (distance != null) {
            // Find variable value in locales score
            return this.visitingScope().getAt(name, distance);
        }
        else if (this.global.contains(name)) {
            // Can't find distance in locales, so it must be global variable
            return this.global.get(name);
        }
        else if (this.externalVariables.containsKey(name.lexeme())) {
            return this.externalVariables.get(name.lexeme());
        }
        else if (this.externalClassRegistry.containsClassName(name.lexeme())) {
            Option<ExternalClass<?>> externalClass =
                this.externalClassRegistry.getByClassNameOrAlias(name.lexeme());
            if (externalClass.present()) {
                return externalClass.get();
            }
        }
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
            .message(DiagnosticMessage.UNDEFINED_VARIABLE, name.lexeme())
            .at(name)
            .build();
    }
}
