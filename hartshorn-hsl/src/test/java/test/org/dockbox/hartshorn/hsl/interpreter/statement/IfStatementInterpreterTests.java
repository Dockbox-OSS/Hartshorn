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

package test.org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.DelegatingInterpreterVisitor;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.interpreter.statement.IfStatementInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import test.org.dockbox.hartshorn.hsl.interpreter.ExecutionCheckStatement;
import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;
import test.org.dockbox.hartshorn.hsl.interpreter.JavaStatement;

public class IfStatementInterpreterTests {

    private static final Token BODY_START_TOKEN = Token.of(TokenType.LEFT_BRACE).lexeme("{").build();

    @Test
    void testIfStatementEvaluatesIfConditionIsTrue() {
        final boolean literalValue = true;
        Assertions.assertTrue(InterpreterUtilities.isTruthy(literalValue));

        final Token conditionToken = Token.of(TokenType.TRUE).lexeme(String.valueOf(literalValue)).build();
        final Expression conditionExpression = new LiteralExpression(conditionToken, true);

        this.assertEvaluatesToTrue(conditionExpression);
    }

    @Test
    void testIfStatementEvaluatesIfConditionIsTruthy() {
        final int literalValue = 1;
        Assertions.assertTrue(InterpreterUtilities.isTruthy(literalValue));

        final Token conditionToken = Token.of(TokenType.NUMBER).lexeme(String.valueOf(literalValue)).build();
        final Expression conditionExpression = new LiteralExpression(conditionToken, literalValue);

        this.assertEvaluatesToTrue(conditionExpression);
    }

    @Test
    void testIfStatementDoesNotEvaluateIfConditionIsFalse() {
        final boolean literalValue = false;
        Assertions.assertFalse(InterpreterUtilities.isTruthy(literalValue));

        final Token conditionToken = Token.of(TokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        final Expression conditionExpression = new LiteralExpression(conditionToken, false);

        this.assertEvaluatesToFalse(conditionExpression);
    }

    @Test
    void testIfStatementDoesNotEvaluateIfConditionIsFalsy() {
        final Object literalValue = null;
        Assertions.assertFalse(InterpreterUtilities.isTruthy(null));

        final Token conditionToken = Token.of(TokenType.NUMBER).lexeme(String.valueOf(literalValue)).build();
        final Expression conditionExpression = new LiteralExpression(conditionToken, literalValue);

        this.assertEvaluatesToFalse(conditionExpression);
    }

    @Test
    void testIfStatementDoesNotEvaluateOrFailIfElseBranchAbsent() {
        final ExecutionCheckStatement trueExecutionCheck = new ExecutionCheckStatement();
        final BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        final boolean literalValue = false;
        Assertions.assertFalse(InterpreterUtilities.isTruthy(literalValue));

        final Token conditionToken = Token.of(TokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        final Expression conditionExpression = new LiteralExpression(conditionToken, false);

        final IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, null);
        final ASTNodeInterpreter<Void, IfStatement> interpreter = new IfStatementInterpreter();
        final InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        Assertions.assertDoesNotThrow(() -> interpreter.interpret(ifStatement, adapter));

        Assertions.assertFalse(trueExecutionCheck.executed());
    }

    @Test
    void testIfStatementUpdatesScopeInThenBranch() {
        final InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        final AtomicBoolean executed = new AtomicBoolean(false);
        final JavaStatement trueExecutionCheck = new JavaStatement(visitor -> {
            executed.set(true);
            Assertions.assertTrue(visitor instanceof DelegatingInterpreterVisitor);

            final DelegatingInterpreterVisitor interpreterVisitor = (DelegatingInterpreterVisitor) visitor;
            final InterpreterAdapter interpreterAdapter = interpreterVisitor.adapter();
            final VariableScope currentScope = interpreterAdapter.visitingScope();
            final VariableScope globalScope = interpreterAdapter.global();
            Assertions.assertNotSame(globalScope, currentScope);
        });
        final BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        final boolean literalValue = true;
        Assertions.assertTrue(InterpreterUtilities.isTruthy(literalValue));

        final Token conditionToken = Token.of(TokenType.TRUE).lexeme(String.valueOf(literalValue)).build();
        final Expression conditionExpression = new LiteralExpression(conditionToken, true);

        final IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, null);
        final ASTNodeInterpreter<Void, IfStatement> interpreter = new IfStatementInterpreter();
        interpreter.interpret(ifStatement, adapter);
        Assertions.assertTrue(executed.get());
    }

    @Test
    void testIfStatementUpdatesScopeInElseBranch() {
        final InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        final AtomicBoolean executed = new AtomicBoolean(false);
        final JavaStatement falseExecutionCheck = new JavaStatement(visitor -> {
            executed.set(true);
            Assertions.assertTrue(visitor instanceof DelegatingInterpreterVisitor);

            final DelegatingInterpreterVisitor interpreterVisitor = (DelegatingInterpreterVisitor) visitor;
            final InterpreterAdapter interpreterAdapter = interpreterVisitor.adapter();
            final VariableScope currentScope = interpreterAdapter.visitingScope();
            final VariableScope globalScope = interpreterAdapter.global();
            Assertions.assertNotSame(globalScope, currentScope);
        });
        final BlockStatement ifFalse = new BlockStatement(BODY_START_TOKEN, List.of(falseExecutionCheck));

        final ExecutionCheckStatement trueExecutionCheck = new ExecutionCheckStatement();
        final BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        final boolean literalValue = false;
        Assertions.assertFalse(InterpreterUtilities.isTruthy(literalValue));

        final Token conditionToken = Token.of(TokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        final Expression conditionExpression = new LiteralExpression(conditionToken, false);

        final IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, ifFalse);
        final ASTNodeInterpreter<Void, IfStatement> interpreter = new IfStatementInterpreter();
        interpreter.interpret(ifStatement, adapter);
        Assertions.assertFalse(trueExecutionCheck.executed());
        Assertions.assertTrue(executed.get());
    }

    private void assertEvaluatesToTrue(final Expression expression) {
        this.assertEvaluatesTo(expression, true);
    }

    private void assertEvaluatesToFalse(final Expression expression) {
        this.assertEvaluatesTo(expression, false);
    }

    private void assertEvaluatesTo(final Expression expression, final boolean evaluatesTo) {
        final ExecutionCheckStatement trueExecutionCheck = new ExecutionCheckStatement();
        final BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        final ExecutionCheckStatement falseExecutionCheck = new ExecutionCheckStatement();
        final BlockStatement ifFalse = new BlockStatement(BODY_START_TOKEN, List.of(falseExecutionCheck));

        final IfStatement ifStatement = new IfStatement(expression, ifTrue, ifFalse);
        final ASTNodeInterpreter<Void, IfStatement> interpreter = new IfStatementInterpreter();
        final InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        interpreter.interpret(ifStatement, adapter);

        Assertions.assertEquals(evaluatesTo, trueExecutionCheck.executed());
        Assertions.assertNotEquals(evaluatesTo, falseExecutionCheck.executed());
    }
}