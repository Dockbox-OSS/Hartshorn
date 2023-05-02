/*
 * Copyright 2019-2023 the original author or authors.
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
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InterpreterState {

    private final Map<String, ExternalInstance> externalVariables = new ConcurrentHashMap<>();
    private final Map<String, ExternalClass<?>> imports = new ConcurrentHashMap<>();
    private final Map<Expression, Integer> locals = new ConcurrentHashMap<>();

    private final Map<String, NativeModule> externalModules;

    private VariableScope global = new VariableScope();
    private VariableScope visitingScope = this.global;
    private final Interpreter owner;

    public InterpreterState(final Map<String, NativeModule> externalModules, final Interpreter owner) {
        this.externalModules = new ConcurrentHashMap<>(externalModules);
        this.owner = owner;
    }

    public VariableScope global() {
        return this.global;
    }

    public VariableScope visitingScope() {
        return this.visitingScope;
    }

    public void restore() {
        this.global = new VariableScope();
        this.visitingScope = this.global;
        this.locals.clear();
    }

    public void externalModule(final String name, final NativeModule module) {
        this.externalModules.put(name, module);
    }

    public void externalModules(final Map<String, NativeModule> externalModules) {
        this.externalModules.putAll(externalModules);
    }

    public Map<String, NativeModule> externalModules() {
        return this.externalModules;
    }

    public void global(final Map<String, Object> globalVariables) {
        globalVariables.forEach((name, instance) -> {
            final TypeView<Object> typeView = this.owner.applicationContext().environment().introspect(instance);
            this.externalVariables.put(name, new ExternalInstance(instance, typeView));
        });
    }

    public void imports(final Map<String, TypeView<?>> imports) {
        imports.forEach((name, type) -> this.imports.put(name, new ExternalClass<>(type)));
    }

    public void enterScope(final VariableScope scope) {
        this.visitingScope = scope;
    }

    public void withScope(final VariableScope scope, final Runnable runnable) {
        final VariableScope previous = this.visitingScope();
        try {
            this.enterScope(scope);
            runnable.run();
        }
        finally {
            this.enterScope(previous);
        }
    }

    public void withNextScope(final Runnable runnable) {
        final VariableScope nextScope = new VariableScope(this.visitingScope());
        this.withScope(nextScope, runnable);
    }

    public Integer distance(final Expression expr) {
        return this.locals.get(expr);
    }

    public void resolve(final Expression expr, final int depth) {
        this.locals.put(expr, depth);
    }

    public Object lookUpVariable(final Token name, final Expression expr) {
        if (name.type() == TokenType.THIS) {
            return this.visitingScope().getAt(name, 1);
        }

        final Integer distance = this.locals.get(expr);
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
        else if (this.imports.containsKey(name.lexeme())) {
            return this.imports.get(name.lexeme());
        }

        throw new ScriptEvaluationError("Undefined variable '" + name.lexeme() + "'.", Phase.INTERPRETING, name);
    }
}
