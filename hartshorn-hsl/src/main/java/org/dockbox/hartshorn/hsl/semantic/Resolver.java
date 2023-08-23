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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalAssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.RangeExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForEachStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.ast.statement.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.objects.Finalizable;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Standard resolver to perform semantic analysis and type checking before a collection
 * of statements is interpreted. This allows illegal references to identifiers to be
 * found and reported early on, preventing potential runtime errors.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class Resolver implements ExpressionVisitor<Void>, StatementVisitor<Void> {

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private final Stack<Map<String, String>> finals = new Stack<>();

    public Resolver(final Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
        INITIALIZER,
        TEST
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS,
    }

    private ClassType currentClass = ClassType.NONE;
    private FunctionType currentFunction = FunctionType.NONE;
    private MoveKeyword.ScopeType currentScopeType = MoveKeyword.ScopeType.NONE;

    @Override
    public Void visit(final BinaryExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final RangeExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final GroupingExpression expr) {
        this.resolve(expr.expression());
        return null;
    }

    @Override
    public Void visit(final LiteralExpression expr) {
        return null;
    }

    @Override
    public Void visit(final AssignExpression expr) {
        this.checkFinal(expr.name());
        this.resolve(expr.value());
        this.resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visit(final LogicalAssignExpression expr) {
        this.checkFinal(expr.name());
        this.resolve(expr.value());
        this.resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visit(final UnaryExpression expr) {
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final PostfixExpression expr) {
        this.resolve(expr.leftExpression());
        return null;
    }

    @Override
    public Void visit(final LogicalExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final BitwiseExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final FunctionCallExpression expr) {
        this.resolve(expr.callee());

        for (final Expression argument : expr.arguments()) {
            this.resolve(argument);
        }

        return null;
    }

    @Override
    public Void visit(final GetExpression expr) {
        this.resolve(expr.object());
        return null;
    }

    @Override
    public Void visit(final SetExpression expr) {
        this.resolve(expr.value());
        this.resolve(expr.object());
        return null;
    }

    @Override
    public Void visit(final ThisExpression expr) {
        if (this.currentClass == ClassType.NONE) {
            throw new ScriptEvaluationError("Cannot use 'this' outside of a class.", Phase.RESOLVING, expr.keyword());
        }
        this.resolveLocal(expr, expr.keyword());
        return null;
    }

    @Override
    public Void visit(final VariableExpression expr) {
        if (!this.scopes.isEmpty() &&
                this.scopes.peek().get(expr.name().lexeme()) == Boolean.FALSE) {
            throw new ScriptEvaluationError("Cannot read local variable in its own initializer.", Phase.RESOLVING, expr.name());
        }
        this.resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visit(final ExpressionStatement statement) {
        this.resolve(statement.expression());
        return null;
    }

    @Override
    public Void visit(final PrintStatement statement) {
        this.resolve(statement.expression());
        return null;
    }

    @Override
    public Void visit(final BlockStatement statement) {
        this.beginScope();
        this.resolve(statement.statements());
        this.endScope();
        return null;
    }

    @Override
    public Void visit(final IfStatement statement) {
        this.resolve(statement.condition());
        this.resolve(statement.thenBranch());
        if (statement.elseBranch() != null) this.resolve(statement.elseBranch());
        return null;
    }

    @Override
    public Void visit(final WhileStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.resolve(statement.condition());
        this.resolve(statement.body());
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final DoWhileStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.beginScope();
        this.resolve(statement.condition());
        this.resolve(statement.body());
        this.endScope();
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final ForStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.beginScope();
        this.resolve(statement.initializer());
        this.resolve(statement.condition());
        this.resolve(statement.increment());
        this.resolve(statement.body());
        this.endScope();
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final ForEachStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.beginScope();
        this.declare(statement.selector().name());
        this.resolve(statement.body());
        this.endScope();
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final RepeatStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.beginScope();
        this.resolve(statement.value());
        this.resolve(statement.body());
        this.endScope();
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final BreakStatement statement) {
        // add this case inside semantic to make sure it inside loop
        if (this.currentScopeType != MoveKeyword.ScopeType.LOOP && this.currentScopeType != MoveKeyword.ScopeType.SWITCH) {
            throw new ScriptEvaluationError("Break can only used be inside loops and switch cases.", Phase.RESOLVING, statement.keyword());
        }
        return null;
    }

    @Override
    public Void visit(final ContinueStatement statement) {
        // add this case inside semantic to make sure it inside loop
        if (this.currentScopeType != MoveKeyword.ScopeType.LOOP) {
            throw new ScriptEvaluationError("Continue can only used be inside loops and switch cases.", Phase.RESOLVING, statement.keyword());
        }
        return null;
    }

    @Override
    public Void visit(final FunctionStatement statement) {
        this.makeFinal(statement, "function");
        this.define(statement.name());
        this.resolveFunction(statement, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visit(final FieldStatement statement) {
        this.makeFinal(statement, "field");
        this.define(statement.name());
        this.resolve(statement);
        return null;
    }

    @Override
    public Void visit(final ConstructorStatement statement) {
        this.define(statement.initializerIdentifier());
        this.resolveFunction(statement, FunctionType.INITIALIZER);
        return null;
    }

    @Override
    public Void visit(final ExtensionStatement statement) {
        final ClassType enclosingClass = this.currentClass;
        this.currentClass = ClassType.CLASS;
        this.declare(statement.className());
        this.resolveFunction(statement.functionStatement(), FunctionType.FUNCTION);
        this.currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final VariableStatement statement) {
        // Resolving a variable declaration adds a new entry to the current innermost scope’s map
        this.declare(statement.name());
        if (statement.initializer() != null) {
            this.makeFinal(statement, "variable");
            this.resolve(statement.initializer());
        }
        this.define(statement.name());
        return null;
    }

    @Override
    public Void visit(final ReturnStatement statement) {
        // Make sure return is inside function
        if (this.currentFunction == FunctionType.NONE) {
            throw new ScriptEvaluationError("Cannot return from top-level code.", Phase.RESOLVING, statement.keyword());
        }
        if (statement.value() != null) {
            if (this.currentFunction == FunctionType.INITIALIZER) {
                throw new ScriptEvaluationError("Cannot return a value from an initializer.", Phase.RESOLVING, statement.keyword());
            }
            this.resolve(statement.value());
        }
        return null;
    }

    @Override
    public Void visit(final ClassStatement statement) {
        final ClassType enclosingClass = this.currentClass;
        this.currentClass = ClassType.CLASS;

        this.declare(statement.name());
        this.makeFinal(statement, "class");

        // Class must not extend itself
        if (statement.superClass() != null &&
                statement.name().lexeme().equals(statement.superClass().name().lexeme())) {
            throw new ScriptEvaluationError("A class cannot inherit from itself.", Phase.RESOLVING, statement.superClass().name());
        }

        // For inheritance
        if (statement.superClass() != null) {
            this.currentClass = ClassType.SUBCLASS;
            this.resolve(statement.superClass());
        }

        // Support super keyword
        if (statement.superClass() != null) {
            this.beginScope();
            this.scopes.peek().put(TokenType.SUPER.representation(), true);
            this.finals.peek().put(TokenType.SUPER.representation(), "instance variable");
        }

        this.beginScope();
        this.scopes.peek().put(TokenType.THIS.representation(), true);
        this.finals.peek().put(TokenType.THIS.representation(), "instance variable");
        for (final FunctionStatement method : statement.methods()) {
            this.resolveFunction(method, FunctionType.METHOD);
        }
        if (statement.constructor() != null) {
            this.resolveFunction(statement.constructor(), FunctionType.INITIALIZER);
        }
        this.define(statement.name());
        this.endScope();
        if (statement.superClass() != null) this.endScope();
        this.currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final NativeFunctionStatement statement) {
        this.makeFinal(statement, "native function");
        this.declare(statement.name());
        this.define(statement.name());

        return null;
    }

    @Override
    public Void visit(final TestStatement statement) {
        final FunctionType enclosingClass = this.currentFunction;
        this.currentFunction = FunctionType.TEST;
        this.declare(statement.name());
        this.resolve(statement.body());
        this.currentFunction = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final ModuleStatement statement) {
        final Map<String, NativeModule> modules = this.interpreter.externalModules();
        final String module = statement.name().lexeme();
        if (!modules.containsKey(module)) {
            throw new ScriptEvaluationError("Cannot find module named '" + module + "'", Phase.RESOLVING, statement.name());
        }
        return null;
    }

    @Override
    public Void visit(final ElvisExpression statement) {
        this.resolve(statement.condition());
        this.resolve(statement.rightExpression());
        return null;
    }

    @Override
    public Void visit(final TernaryExpression statement) {
        this.resolve(statement.condition());
        this.resolve(statement.firstExpression());
        this.resolve(statement.secondExpression());
        return null;
    }

    @Override
    public Void visit(final ArraySetExpression expr) {
        this.define(expr.name());
        this.resolve(expr.index());
        this.resolve(expr.value());
        return null;
    }

    @Override
    public Void visit(final ArrayGetExpression expr) {
        this.define(expr.name());
        this.resolve(expr.index());
        return null;
    }

    @Override
    public Void visit(final ArrayLiteralExpression expr) {
        for (final Expression element : expr.elements()) {
            this.resolve(element);
        }
        return null;
    }

    @Override
    public Void visit(final ArrayComprehensionExpression expr) {
        this.resolve(expr.collection());
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;

        this.beginScope();
        this.declare(expr.selector());
        {
            this.beginScope();
            this.resolve(expr.expression());
            if (expr.condition() != null) {
                this.resolve(expr.condition());
            }
            if (expr.elseExpression() != null) {
                this.resolve(expr.elseExpression());
            }
            this.endScope();
        }
        this.endScope();

        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final PrefixExpression expr) {
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final InfixExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final SuperExpression expr) {
        if (this.currentClass == ClassType.NONE) {
            throw new ScriptEvaluationError("Cannot use 'super' outside of a class.", Phase.RESOLVING, expr.keyword());
        }
        else if (this.currentClass != ClassType.SUBCLASS) {
            throw new ScriptEvaluationError("Cannot use 'super' in a class with no super class.", Phase.RESOLVING, expr.keyword());
        }
        this.resolveLocal(expr, expr.keyword());
        return null;
    }

    @Override
    public Void visit(final SwitchStatement statement) {
        this.resolve(statement.expression());
        for (final SwitchCase switchCase : statement.cases()) {
            this.resolve(switchCase);
        }
        this.beginScope();
        this.resolve(statement.defaultCase());
        this.endScope();
        return null;
    }

    @Override
    public Void visit(final SwitchCase statement) {
        if (!statement.isDefault()) {
            this.resolve(statement.expression());
        }

        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.SWITCH;
        this.beginScope();
        this.resolve(statement.body());
        this.endScope();
        this.currentScopeType = enclosingType;

        return null;
    }

    private void beginScope() {
        this.scopes.push(new HashMap<>());
        this.finals.push(new HashMap<>());
    }

    private void endScope() {
        this.scopes.pop();
        this.finals.pop();
    }

    public void resolve(final List<Statement> stmtList) {
        for (final Statement statement : stmtList) {
            this.resolve(statement);
        }
    }

    private void resolve(final Statement stmt) {
        stmt.accept(this);
    }

    private void resolve(final Expression expr) {
        expr.accept(this);
    }

    private void resolveLocal(final Expression expr, final Token name) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            if (this.scopes.get(i).containsKey(name.lexeme())) {
                this.interpreter.resolve(expr, this.scopes.size() - 1 - i);
                return;
            }
        }
        // Not found. Assume it is global.
    }

    private void resolveFunction(final ParametricExecutableStatement executable, final FunctionType type) {
        final FunctionType enclosingFunction = this.currentFunction;
        this.currentFunction = type;

        this.beginScope();
        for (final Parameter param : executable.parameters()) {
            this.declare(param.name());
            this.define(param.name());
        }
        this.resolve(executable.statements());
        this.endScope();

        this.currentFunction = enclosingFunction;
    }

    private void declare(final Token name) {
        if (this.scopes.isEmpty()) return;

        final Map<String, Boolean> scope = this.scopes.peek();

        // Never declare variable twice in same scope
        if (scope.containsKey(name.lexeme())) {
            throw new ScriptEvaluationError("Variable with name '%s' already declared in this scope.".formatted(name.lexeme()), Phase.RESOLVING, name);
        }
        scope.put(name.lexeme(), false);
    }

    private void define(final Token name) {
        // set the variable’s value in the scope map to true to mark it as fully initialized and available for use
        if (this.scopes.isEmpty()) return;
        this.checkFinal(name);
        this.scopes.peek().put(name.lexeme(), true);
    }

    private void checkFinal(final Token name) {
        if (this.finals.peek().containsKey(name.lexeme())) {
            final String existingWhat = this.finals.peek().get(name.lexeme());
            throw new ScriptEvaluationError("Cannot reassign final %s '%s'.".formatted(existingWhat, name.lexeme()), Phase.RESOLVING, name);
        }
    }

    private <R extends Finalizable & NamedNode> void makeFinal(final R node, final String what) {
        // Unlike scopes, finals need to be tracked even in the global scope.
        if (this.finals.isEmpty()) this.finals.push(new HashMap<>());
        this.checkFinal(node.name());
        if (node.isFinal()) {
            this.finals.peek().put(node.name().lexeme(), what);
        }
    }
}
