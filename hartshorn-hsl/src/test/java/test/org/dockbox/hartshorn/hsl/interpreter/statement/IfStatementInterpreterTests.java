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

package test.org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.DelegatingInterpreterVisitor;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.interpreter.statement.IfStatementInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.interpreter.ExecutionCheckStatement;
import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;
import test.org.dockbox.hartshorn.hsl.interpreter.JavaStatement;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class IfStatementInterpreterTests {

    private static final Token BODY_START_TOKEN = Token.of(InterpreterTestHelper.defaultTokenPairs().block().open()).build();

    @Test
    void ifStatementEvaluatesIfConditionIsTrue() {
        boolean literalValue = true;
        assertThat(InterpreterUtilities.isTruthy(literalValue)).isTrue();

        Token conditionToken = Token.of(LiteralTokenType.TRUE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, true);

        this.assertEvaluatesToTrue(conditionExpression);
    }

    @Test
    void ifStatementEvaluatesIfConditionIsTruthy() {
        int literalValue = 1;
        assertThat(InterpreterUtilities.isTruthy(literalValue)).isTrue();

        Token conditionToken = Token.of(LiteralTokenType.NUMBER).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, literalValue);

        this.assertEvaluatesToTrue(conditionExpression);
    }

    @Test
    void ifStatementDoesNotEvaluateIfConditionIsFalse() {
        boolean literalValue = false;
        assertThat(InterpreterUtilities.isTruthy(literalValue)).isFalse();

        Token conditionToken = Token.of(LiteralTokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, false);

        this.assertEvaluatesToFalse(conditionExpression);
    }

    @Test
    void ifStatementDoesNotEvaluateIfConditionIsFalsy() {
        Object literalValue = null;
        assertThat(InterpreterUtilities.isTruthy(null)).isFalse();

        Token conditionToken = Token.of(LiteralTokenType.NUMBER).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, literalValue);

        this.assertEvaluatesToFalse(conditionExpression);
    }

    @Test
    void ifStatementDoesNotEvaluateOrFailIfElseBranchAbsent() {
        ExecutionCheckStatement trueExecutionCheck = new ExecutionCheckStatement();
        BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        boolean literalValue = false;
        assertThat(InterpreterUtilities.isTruthy(literalValue)).isFalse();

        Token conditionToken = Token.of(LiteralTokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, false);

        IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, null);
        ASTNodeInterpreter<Void, IfStatement> statementInterpreter = new IfStatementInterpreter();
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        assertThatCode(() -> statementInterpreter.interpret(ifStatement, interpreter)).doesNotThrowAnyException();

        assertThat(trueExecutionCheck.executed()).isFalse();
    }

    @Test
    void ifStatementUpdatesScopeInThenBranch() {
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        AtomicBoolean executed = new AtomicBoolean(false);
        JavaStatement trueExecutionCheck = new JavaStatement(visitor -> {
            executed.set(true);
            assertThat(visitor).isInstanceOf(DelegatingInterpreterVisitor.class);

            DelegatingInterpreterVisitor interpreterVisitor = (DelegatingInterpreterVisitor) visitor;
            Interpreter visitorInterpreter = interpreterVisitor.interpreter();
            VariableScope currentScope = visitorInterpreter.visitingScope();
            VariableScope globalScope = visitorInterpreter.global();
            assertThat(currentScope).isNotSameAs(globalScope);
        });
        BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        boolean literalValue = true;
        assertThat(InterpreterUtilities.isTruthy(literalValue)).isTrue();

        Token conditionToken = Token.of(LiteralTokenType.TRUE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, true);

        IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, null);
        ASTNodeInterpreter<Void, IfStatement> statementInterpreter = new IfStatementInterpreter();
        statementInterpreter.interpret(ifStatement, interpreter);
        assertThat(executed.get()).isTrue();
    }

    @Test
    void ifStatementUpdatesScopeInElseBranch() {
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        AtomicBoolean executed = new AtomicBoolean(false);
        JavaStatement falseExecutionCheck = new JavaStatement(visitor -> {
            executed.set(true);
            assertThat(visitor).isInstanceOf(DelegatingInterpreterVisitor.class);

            DelegatingInterpreterVisitor interpreterVisitor = (DelegatingInterpreterVisitor) visitor;
            Interpreter visitorInterpreter = interpreterVisitor.interpreter();
            VariableScope currentScope = visitorInterpreter.visitingScope();
            VariableScope globalScope = visitorInterpreter.global();
            assertThat(currentScope).isNotSameAs(globalScope);
        });
        BlockStatement ifFalse = new BlockStatement(BODY_START_TOKEN, List.of(falseExecutionCheck));

        ExecutionCheckStatement trueExecutionCheck = new ExecutionCheckStatement();
        BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        boolean literalValue = false;
        assertThat(InterpreterUtilities.isTruthy(literalValue)).isFalse();

        Token conditionToken = Token.of(LiteralTokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, false);

        IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, ifFalse);
        ASTNodeInterpreter<Void, IfStatement> statementInterpreter = new IfStatementInterpreter();
        statementInterpreter.interpret(ifStatement, interpreter);
        assertThat(trueExecutionCheck.executed()).isFalse();
        assertThat(executed.get()).isTrue();
    }

    private void assertEvaluatesToTrue(Expression expression) {
        this.assertEvaluatesTo(expression, true);
    }

    private void assertEvaluatesToFalse(Expression expression) {
        this.assertEvaluatesTo(expression, false);
    }

    private void assertEvaluatesTo(Expression expression, boolean evaluatesTo) {
        ExecutionCheckStatement trueExecutionCheck = new ExecutionCheckStatement();
        BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        ExecutionCheckStatement falseExecutionCheck = new ExecutionCheckStatement();
        BlockStatement ifFalse = new BlockStatement(BODY_START_TOKEN, List.of(falseExecutionCheck));

        IfStatement ifStatement = new IfStatement(expression, ifTrue, ifFalse);
        ASTNodeInterpreter<Void, IfStatement> statementInterpreter = new IfStatementInterpreter();
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        statementInterpreter.interpret(ifStatement, interpreter);

        assertThat(trueExecutionCheck.executed()).isEqualTo(evaluatesTo);
        assertThat(falseExecutionCheck.executed()).isNotEqualTo(evaluatesTo);
    }
}
