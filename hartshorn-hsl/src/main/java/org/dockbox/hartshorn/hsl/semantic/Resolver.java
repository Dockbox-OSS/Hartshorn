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

package org.dockbox.hartshorn.hsl.semantic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.MoveKeyword.ScopeType;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.extension.CustomExpression;
import org.dockbox.hartshorn.hsl.extension.CustomStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.Finalizable;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * Standard resolver to perform semantic analysis and type checking before a collection
 * of statements is interpreted. This allows illegal references to identifiers to be
 * found and reported early on, preventing potential runtime errors.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class Resolver {

    public enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
        INITIALIZER,
        TEST,
    }

    public enum ClassType {
        NONE,
        CLASS,
        SUBCLASS,
    }

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private final Stack<Map<String, String>> finals = new Stack<>();
    private final ResolverVisitor visitor = new ResolverVisitor(this);

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private ClassType currentClass = ClassType.NONE;
    private FunctionType currentFunction = FunctionType.NONE;
    private MoveKeyword.ScopeType currentScopeType = MoveKeyword.ScopeType.NONE;

    public Interpreter interpreter() {
        return this.interpreter;
    }

    public boolean hasDefinedScopes() {
        return !this.scopes.isEmpty();
    }

    public Map<String, Boolean> peekScope() {
        return this.scopes.peek();
    }

    public Map<String, String> peekFinal() {
        return this.finals.peek();
    }

    public ClassType currentClass() {
        return this.currentClass;
    }

    public FunctionType currentFunction() {
        return this.currentFunction;
    }

    public ScopeType currentScopeType() {
        return this.currentScopeType;
    }

    public Resolver currentClass(ClassType currentClass) {
        this.currentClass = currentClass;
        return this;
    }

    public Resolver currentFunction(FunctionType currentFunction) {
        this.currentFunction = currentFunction;
        return this;
    }

    public Resolver currentScopeType(ScopeType currentScopeType) {
        this.currentScopeType = currentScopeType;
        return this;
    }

    public void beginScope() {
        this.scopes.push(new HashMap<>());
        this.finals.push(new HashMap<>());
    }

    public void endScope() {
        this.scopes.pop();
        this.finals.pop();
    }

    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            this.resolve(statement);
        }
    }

    public void resolve(Statement statement) {
        if (statement instanceof CustomStatement<?> customStatement) {
            customStatement.resolve(this);
        }
        else {
            statement.accept(this.visitor);
        }
    }

    public void resolve(Expression expression) {
        if (expression instanceof CustomExpression<?> customExpression) {
            customExpression.resolve(this);
        }
        else {
            expression.accept(this.visitor);
        }
    }

    public void resolveLocal(Expression expression, Token name) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            if (this.scopes.get(i).containsKey(name.lexeme())) {
                this.interpreter.resolve(expression, this.scopes.size() - 1 - i);
                return;
            }
        }
        // Not found. Assume it is global.
    }

    public void resolveFunction(ParametricExecutableStatement executable, FunctionType type) {
        FunctionType enclosingFunction = this.currentFunction;
        this.currentFunction = type;

        this.beginScope();
        for (Parameter param : executable.parameters()) {
            this.declare(param.name());
            this.define(param.name());
        }
        this.resolve(executable.statements());
        this.endScope();

        this.currentFunction = enclosingFunction;
    }

    public void declare(Token name) {
        if (this.scopes.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = this.scopes.peek();

        // Never declare variable twice in same scope
        if (scope.containsKey(name.lexeme())) {
            throw new ScriptEvaluationError("Variable with name '%s' already declared in this scope.".formatted(name.lexeme()), Phase.RESOLVING, name);
        }
        scope.put(name.lexeme(), false);
    }

    public void define(Token name) {
        // set the variable’s value in the scope map to true to mark it as fully initialized and available for use
        if (this.scopes.isEmpty()) {
            return;
        }
        this.checkFinal(name);
        this.scopes.peek().put(name.lexeme(), true);
    }

    public void checkFinal(Token name) {
        if (this.finals.peek().containsKey(name.lexeme())) {
            String existingWhat = this.finals.peek().get(name.lexeme());
            throw new ScriptEvaluationError("Cannot reassign final %s '%s'.".formatted(existingWhat, name.lexeme()), Phase.RESOLVING, name);
        }
    }

    public <R extends Finalizable & NamedNode> void makeFinal(R node, String what) {
        // Unlike scopes, finals need to be tracked even in the global scope.
        if (this.finals.isEmpty()) {
            this.finals.push(new HashMap<>());
        }
        this.checkFinal(node.name());
        if (node.isFinal()) {
            this.finals.peek().put(node.name().lexeme(), what);
        }
    }
}
