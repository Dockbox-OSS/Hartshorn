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
import org.dockbox.hartshorn.hsl.ast.statement.FieldGetStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldSetStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForEachStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.Map;

/**
 * Support for {@link Resolver semantic analysis} of the AST. This visitor is used to resolve all references originating
 * in the AST. The visitor itself doesn't track the resolved references, but instead delegates to the {@link Resolver}
 * to do so.
 *
 * @see Resolver
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ResolverVisitor implements ExpressionVisitor<Void>, StatementVisitor<Void> {

    private final Resolver resolver;

    public ResolverVisitor(Resolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Void visit(BinaryExpression expression) {
        this.resolve(expression.leftExpression());
        this.resolve(expression.rightExpression());
        return null;
    }

    private void resolve(Expression expression) {
        this.resolver.resolve(expression);
    }

    private void resolve(Statement statement) {
        this.resolver.resolve(statement);
    }

    @Override
    public Void visit(RangeExpression expression) {
        this.resolve(expression.leftExpression());
        this.resolve(expression.rightExpression());
        return null;
    }

    @Override
    public Void visit(GroupingExpression expression) {
        this.resolve(expression.expression());
        return null;
    }

    @Override
    public Void visit(LiteralExpression expression) {
        return null;
    }

    @Override
    public Void visit(AssignExpression expression) {
        this.resolver.checkFinal(expression.name());
        this.resolve(expression.value());
        this.resolver.resolveLocal(expression, expression.name());
        return null;
    }

    @Override
    public Void visit(LogicalAssignExpression expression) {
        this.resolver.checkFinal(expression.name());
        this.resolve(expression.value());
        this.resolver.resolveLocal(expression, expression.name());
        return null;
    }

    @Override
    public Void visit(UnaryExpression expression) {
        this.resolve(expression.rightExpression());
        return null;
    }

    @Override
    public Void visit(PostfixExpression expression) {
        this.resolve(expression.leftExpression());
        return null;
    }

    @Override
    public Void visit(LogicalExpression expression) {
        this.resolve(expression.leftExpression());
        this.resolve(expression.rightExpression());
        return null;
    }

    @Override
    public Void visit(BitwiseExpression expression) {
        this.resolve(expression.leftExpression());
        this.resolve(expression.rightExpression());
        return null;
    }

    @Override
    public Void visit(FunctionCallExpression expression) {
        this.resolve(expression.callee());

        for (Expression argument : expression.arguments()) {
            this.resolve(argument);
        }

        return null;
    }

    @Override
    public Void visit(GetExpression expression) {
        this.resolve(expression.object());
        return null;
    }

    @Override
    public Void visit(SetExpression expression) {
        this.resolve(expression.value());
        this.resolve(expression.object());
        return null;
    }

    @Override
    public Void visit(ThisExpression expression) {
        if (this.resolver.currentClassType() == ClassType.NONE) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.CANNOT_USE_X_OUTSIDE_CLASS, expression.keyword().lexeme())
                    .at(expression.keyword())
                    .build();
        }
        this.resolver.resolveLocal(expression, expression.keyword());
        return null;
    }

    @Override
    public Void visit(VariableExpression expression) {
        if (this.resolver.hasDefinedScopes()) {
            Boolean initialized = this.resolver.peekScope().get(expression.name().lexeme());
            if (initialized != null && !initialized) {
                throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                        .message(DiagnosticMessage.UNDEFINED_VARIABLE, expression.name().lexeme())
                        .at(expression.name())
                        .build();
            }
        }
        this.resolver.resolveLocal(expression, expression.name());
        return null;
    }

    @Override
    public Void visit(ExpressionStatement statement) {
        this.resolve(statement.expression());
        return null;
    }

    @Override
    public Void visit(BlockStatement statement) {
        this.resolver.beginScope();
        this.resolver.resolve(statement.statements());
        this.resolver.endScope();
        return null;
    }

    @Override
    public Void visit(IfStatement statement) {
        this.resolve(statement.condition());
        this.resolve(statement.thenBranch());
        if (statement.elseBranch() != null) {
            this.resolve(statement.elseBranch());
        }
        return null;
    }

    @Override
    public Void visit(WhileStatement statement) {
        FlowControlKeyword.ScopeType enclosingType = this.resolver.currentScopeType();
        this.resolver.currentScopeType(FlowControlKeyword.ScopeType.LOOP);
        this.resolve(statement.condition());
        this.resolve(statement.body());
        this.resolver.currentScopeType(enclosingType);
        return null;
    }

    @Override
    public Void visit(DoWhileStatement statement) {
        FlowControlKeyword.ScopeType enclosingType = this.resolver.currentScopeType();
        this.resolver.currentScopeType(FlowControlKeyword.ScopeType.LOOP);
        this.resolver.beginScope();
        this.resolve(statement.condition());
        this.resolve(statement.body());
        this.resolver.endScope();
        this.resolver.currentScopeType(enclosingType);
        return null;
    }

    @Override
    public Void visit(ForStatement statement) {
        FlowControlKeyword.ScopeType enclosingType = this.resolver.currentScopeType();
        this.resolver.currentScopeType(FlowControlKeyword.ScopeType.LOOP);
        this.resolver.beginScope();
        this.resolve(statement.initializer());
        this.resolve(statement.condition());
        this.resolve(statement.increment());
        this.resolve(statement.body());
        this.resolver.endScope();
        this.resolver.currentScopeType(enclosingType);
        return null;
    }

    @Override
    public Void visit(ForEachStatement statement) {
        FlowControlKeyword.ScopeType enclosingType = this.resolver.currentScopeType();
        this.resolver.currentScopeType(FlowControlKeyword.ScopeType.LOOP);
        this.resolver.beginScope();
        this.resolver.declare(statement.selector().name());
        this.resolve(statement.body());
        this.resolver.endScope();
        this.resolver.currentScopeType(enclosingType);
        return null;
    }

    @Override
    public Void visit(RepeatStatement statement) {
        FlowControlKeyword.ScopeType enclosingType = this.resolver.currentScopeType();
        this.resolver.currentScopeType(FlowControlKeyword.ScopeType.LOOP);
        this.resolver.beginScope();
        this.resolve(statement.value());
        this.resolve(statement.body());
        this.resolver.endScope();
        this.resolver.currentScopeType(enclosingType);
        return null;
    }

    @Override
    public Void visit(BreakStatement statement) {
        // add this case inside semantic to make sure it inside loop
        if (this.resolver.currentScopeType() != FlowControlKeyword.ScopeType.LOOP && this.resolver.currentScopeType() != FlowControlKeyword.ScopeType.SWITCH) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.X_CAN_ONLY_BE_USED_IN_LOOPS_AND_SWITCHES, statement.keyword().lexeme())
                    .at(statement.keyword())
                    .build();
        }
        return null;
    }

    @Override
    public Void visit(ContinueStatement statement) {
        // add this case inside semantic to make sure it inside loop
        if (this.resolver.currentScopeType() != FlowControlKeyword.ScopeType.LOOP) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.X_CAN_ONLY_BE_USED_IN_LOOPS_AND_SWITCHES, statement.keyword().lexeme())
                    .at(statement.keyword())
                    .build();
        }
        return null;
    }

    @Override
    public Void visit(FunctionStatement statement) {
        this.resolver.makeFinal(statement, "function");
        this.resolver.define(statement.name());
        this.resolver.resolveFunction(statement, FunctionType.INLINE_FUNCTION);
        return null;
    }

    @Override
    public Void visit(FieldStatement statement) {
        this.resolver.define(statement.name());
        this.resolver.makeFinal(statement, "field");

        if (statement.getter() != null) {
            this.resolve(statement.getter());
        }
        if (statement.setter() != null) {
            this.resolve(statement.setter());
        }
        if (statement.initializer() != null) {
            this.resolve(statement.initializer());
        }
        return null;
    }

    @Override
    public Void visit(final FieldGetStatement statement) {
        if (statement.hasBody()) {
            this.resolver.resolveFunction(statement, FunctionType.FIELD_MEMBER);
        }
        return null;
    }

    @Override
    public Void visit(final FieldSetStatement statement) {
        if (statement.hasBody()) {
            this.resolver.resolveFunction(statement, FunctionType.FIELD_MEMBER);
        }
        return null;
    }

    @Override
    public Void visit(ConstructorStatement statement) {
        if (this.resolver.currentClassType() == ClassType.NONE) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.CONSTRUCTOR_OUTSIDE_CLASS)
                    .at(statement.keyword())
                    .build();
        }
        this.resolver.define(statement.initializerIdentifier());
        this.resolver.resolveFunction(statement, FunctionType.INITIALIZER);
        return null;
    }

    @Override
    public Void visit(VariableStatement statement) {
        // Resolving a variable declaration adds a new entry to the current innermost scope’s map
        this.resolver.declare(statement.name());
        if (statement.initializer() != null) {
            this.resolver.makeFinal(statement, "variable");
            this.resolve(statement.initializer());
        }
        this.resolver.define(statement.name());
        return null;
    }

    @Override
    public Void visit(ReturnStatement statement) {
        switch (this.resolver.currentFunction()) {
            case NONE -> throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.TOP_LEVEL_RETURN)
                    .at(statement.keyword())
                    .build();
            case INLINE_FUNCTION, CLASS_FUNCTION -> {
                if (statement.returnType() != ReturnStatement.ReturnType.RETURN) {
                    throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                            .message(DiagnosticMessage.FUNCTION_CANNOT_YIELD)
                            .at(statement.keyword())
                            .build();
                }
            }
            case INITIALIZER -> {
                if (statement.expression() != null) {
                    throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                            .message(DiagnosticMessage.INITIALIZER_RETURN)
                            .at(statement.keyword())
                            .build();
                }
            }
            case TEST-> {
                if (statement.returnType() != ReturnStatement.ReturnType.YIELD) {
                    throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                            .message(DiagnosticMessage.TEST_BLOCK_RETURN)
                            .at(statement.keyword())
                            .build();
                }
            }
            case FIELD_MEMBER -> {
                if (statement.returnType() != ReturnStatement.ReturnType.YIELD) {
                    throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                            .message(DiagnosticMessage.FIELD_MEMBER_RETURN)
                            .at(statement.keyword())
                            .build();
                }
            }
            default -> {
                throw new IllegalStateException("Unrecognized function type: " + this.resolver.currentFunction());
            }
        }

        if (statement.expression() != null) {
            this.resolve(statement.expression());
        }
        return null;
    }

    @Override
    public Void visit(ClassStatement statement) {
        ClassType enclosingClass = this.resolver.currentClassType();
        this.resolver.currentClassType(ClassType.CLASS);

        this.resolver.declare(statement.name());
        this.resolver.makeFinal(statement, "class");

        // Class must not extend itself
        if (statement.superClass() != null &&
                statement.name().lexeme().equals(statement.superClass().name().lexeme())) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.CLASS_CANNOT_EXTEND_SELF)
                    .at(statement.superClass().name())
                    .build();
        }

        // For inheritance
        if (statement.superClass() != null) {
            this.resolver.currentClassType(ClassType.SUBCLASS);
            this.resolve(statement.superClass());
        }

        // Support super keyword
        if (statement.superClass() != null) {
            this.resolver.beginScope();
            this.resolver.peekScope().put(ObjectTokenType.SUPER.representation(), true);
            this.resolver.peekFinal().put(ObjectTokenType.SUPER.representation(), "instance variable");
        }

        this.resolver.beginScope();
        this.resolver.peekScope().put(ObjectTokenType.THIS.representation(), true);
        this.resolver.peekFinal().put(ObjectTokenType.THIS.representation(), "instance variable");
        for (FieldStatement field : statement.fields()) {
            this.resolve(field);
        }
        for (FunctionStatement method : statement.methods()) {
            this.resolver.resolveFunction(method, FunctionType.CLASS_FUNCTION);
        }
        if (statement.constructor() != null) {
            this.resolver.resolveFunction(statement.constructor(), FunctionType.INITIALIZER);
        }
        this.resolver.define(statement.name());
        this.resolver.endScope();
        if (statement.superClass() != null) {
            this.resolver.endScope();
        }
        this.resolver.currentClassType(enclosingClass);
        return null;
    }

    @Override
    public Void visit(NativeFunctionStatement statement) {
        this.resolver.makeFinal(statement, "native function");
        this.resolver.declare(statement.name());
        this.resolver.define(statement.name());

        return null;
    }

    @Override
    public Void visit(TestStatement statement) {
        FunctionType enclosingFunction = this.resolver.currentFunction();
        this.resolver.currentFunction(FunctionType.TEST);
        this.resolver.declare(statement.name());
        this.resolve(statement.body());
        this.resolver.currentFunction(enclosingFunction);
        return null;
    }

    @Override
    public Void visit(ModuleStatement statement) {
        Map<String, NativeModule> modules = this.resolver.interpreter().state().externalModules();
        String module = statement.name().lexeme();
        if (!modules.containsKey(module)) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.MISSING_MODULE, module)
                    .at(statement.name())
                    .build();
        }
        return null;
    }

    @Override
    public Void visit(ElvisExpression statement) {
        this.resolve(statement.condition());
        this.resolve(statement.rightExpression());
        return null;
    }

    @Override
    public Void visit(TernaryExpression statement) {
        this.resolve(statement.condition());
        this.resolve(statement.firstExpression());
        this.resolve(statement.secondExpression());
        return null;
    }

    @Override
    public Void visit(ArraySetExpression expression) {
        this.resolver.define(expression.name());
        this.resolve(expression.index());
        this.resolve(expression.value());
        return null;
    }

    @Override
    public Void visit(ArrayGetExpression expression) {
        this.resolver.define(expression.name());
        this.resolve(expression.index());
        return null;
    }

    @Override
    public Void visit(ArrayLiteralExpression expression) {
        for (Expression element : expression.elements()) {
            this.resolve(element);
        }
        return null;
    }

    @Override
    public Void visit(ArrayComprehensionExpression expression) {
        this.resolve(expression.collection());
        FlowControlKeyword.ScopeType enclosingType = this.resolver.currentScopeType();
        this.resolver.currentScopeType(FlowControlKeyword.ScopeType.LOOP);

        this.resolver.beginScope();
        this.resolver.declare(expression.selector());
        {
            this.resolver.beginScope();
            this.resolve(expression.expression());
            if (expression.condition() != null) {
                this.resolve(expression.condition());
            }
            if (expression.elseExpression() != null) {
                this.resolve(expression.elseExpression());
            }
            this.resolver.endScope();
        }
        this.resolver.endScope();

        this.resolver.currentScopeType(enclosingType);
        return null;
    }

    @Override
    public Void visit(PrefixExpression expression) {
        this.resolve(expression.rightExpression());
        return null;
    }

    @Override
    public Void visit(InfixExpression expression) {
        this.resolve(expression.leftExpression());
        this.resolve(expression.rightExpression());
        return null;
    }

    @Override
    public Void visit(SuperExpression expression) {
        if (this.resolver.currentClassType() == ClassType.NONE) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.CANNOT_USE_X_OUTSIDE_CLASS, expression.keyword().lexeme())
                    .at(expression.keyword())
                    .build();
        }
        else if (this.resolver.currentClassType() != ClassType.SUBCLASS) {
            throw ScriptEvaluationError.builder(Phase.SEMANTIC_ANALYSIS)
                    .message(DiagnosticMessage.CANNOT_USE_X_WITHOUT_SUPER_CLASS, expression.keyword().lexeme())
                    .at(expression.keyword())
                    .build();
        }
        this.resolver.resolveLocal(expression, expression.keyword());
        return null;
    }

    @Override
    public Void visit(SwitchStatement statement) {
        this.resolve(statement.expression());
        for (SwitchCase switchCase : statement.cases()) {
            this.resolve(switchCase);
        }
        this.resolver.beginScope();
        this.resolve(statement.defaultCase());
        this.resolver.endScope();
        return null;
    }

    @Override
    public Void visit(SwitchCase statement) {
        if (!statement.isDefault()) {
            this.resolve(statement.expression());
        }

        FlowControlKeyword.ScopeType enclosingType = this.resolver.currentScopeType();
        this.resolver.currentScopeType(FlowControlKeyword.ScopeType.SWITCH);
        this.resolver.beginScope();
        this.resolve(statement.body());
        this.resolver.endScope();
        this.resolver.currentScopeType(enclosingType);

        return null;
    }
}
