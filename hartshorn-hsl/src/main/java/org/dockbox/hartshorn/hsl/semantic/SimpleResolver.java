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

package org.dockbox.hartshorn.hsl.semantic;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.FlowControlKeyword;
import org.dockbox.hartshorn.hsl.ast.FlowControlKeyword.ScopeType;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.extension.CustomExpression;
import org.dockbox.hartshorn.hsl.extension.CustomStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.Finalizable;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Standard resolver to perform semantic analysis and type checking before a collection of
 * statements is interpreted. This allows illegal references to identifiers to be found and reported
 * early on, preventing potential runtime errors.
 *
 * <p>This resolver delegates most of its work to a {@link ResolverVisitor}, which is a visitor
 * that resolves the appropriate nodes in the AST. The resolver itself is responsible for managing
 * the tracking of resolved items.
 *
 * @see ResolverVisitor
 * 
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public class SimpleResolver implements Resolver {

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private final Stack<Map<String, String>> finals = new Stack<>();
    private final ResolverVisitor visitor = new ResolverVisitor(this);

    private ClassType currentClass = ClassType.NONE;
    private FunctionType currentFunction = FunctionType.NONE;
    private FlowControlKeyword.ScopeType currentScopeType = FlowControlKeyword.ScopeType.NONE;

    public SimpleResolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Interpreter interpreter() {
        return this.interpreter;
    }

    @Override
    public boolean hasDefinedScopes() {
        return !this.scopes.isEmpty();
    }

    @Override
    public Map<String, Boolean> peekScope() {
        return this.scopes.peek();
    }

    @Override
    public Map<String, String> peekFinal() {
        return this.finals.peek();
    }

    @Override
    public ClassType currentClassType() {
        return this.currentClass;
    }

    @Override
    public FunctionType currentFunction() {
        return this.currentFunction;
    }

    @Override
    public ScopeType currentScopeType() {
        return this.currentScopeType;
    }

    @Override
    public Resolver currentClassType(ClassType currentClass) {
        this.currentClass = currentClass;
        return this;
    }

    @Override
    public Resolver currentFunction(FunctionType currentFunction) {
        this.currentFunction = currentFunction;
        return this;
    }

    @Override
    public Resolver currentScopeType(ScopeType currentScopeType) {
        this.currentScopeType = currentScopeType;
        return this;
    }

    @Override
    public void beginScope() {
        this.scopes.push(new HashMap<>());
        this.finals.push(new HashMap<>());
    }

    @Override
    public void endScope() {
        this.scopes.pop();
        this.finals.pop();
    }

    @Override
    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            this.resolve(statement);
        }
    }

    @Override
    public void resolve(Statement statement) {
        if (statement instanceof CustomStatement<?> customStatement) {
            customStatement.resolve(this);
        }
        else {
            statement.accept(this.visitor);
        }
    }

    @Override
    public void resolve(Expression expression) {
        if (expression instanceof CustomExpression<?> customExpression) {
            customExpression.resolve(this);
        }
        else {
            expression.accept(this.visitor);
        }
    }

    @Override
    public void resolveLocal(Expression expression, Token name) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            if (this.scopes.get(i).containsKey(name.lexeme())) {
                this.interpreter.resolve(expression, this.scopes.size() - 1 - i);
                return;
            }
        }
        // Not found. Assume it is global.
    }

    @Override
    public void resolveFunction(ParametricExecutableStatement executable, FunctionType type) {
        FunctionType enclosingFunction = this.currentFunction;
        this.currentFunction = type;

        this.beginScope();
        for (Parameter parameter : executable.parameters()) {
            this.declare(parameter.name());
            this.define(parameter.name());
        }
        this.resolve(executable.statements());
        this.endScope();

        this.currentFunction = enclosingFunction;
    }

    @Override
    public void declare(Token name) {
        if (this.scopes.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = this.scopes.peek();

        // Never declare variable twice in same scope
        if (scope.containsKey(name.lexeme())) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                .message(DiagnosticMessage.VARIABLE_ALREADY_DECLARED, name.lexeme())
                .at(name)
                .build();
        }
        scope.put(name.lexeme(), false);
    }

    @Override
    public void define(Token name) {
        if (this.scopes.isEmpty()) {
            return;
        }
        this.checkFinal(name);
        this.scopes.peek().put(name.lexeme(), true);
    }

    @Override
    public void checkFinal(Token name) {
        if (this.finals.peek().containsKey(name.lexeme())) {
            String existingWhat = this.finals.peek().get(name.lexeme());
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                .message(DiagnosticMessage.ILLEGAL_FINAL_X_REASSIGNMENT,
                    existingWhat,
                    name.lexeme())
                .at(name)
                .build();
        }
    }

    @Override
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
