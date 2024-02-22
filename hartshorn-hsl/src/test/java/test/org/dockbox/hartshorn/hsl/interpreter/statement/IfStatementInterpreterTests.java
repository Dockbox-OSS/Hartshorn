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

package test.org.dockbox.hartshorn.hsl.interpreter.statement;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.hsl.interpreter.ExecutionCheckStatement;
import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;
import test.org.dockbox.hartshorn.hsl.interpreter.JavaStatement;

public class IfStatementInterpreterTests {

    private static final Token BODY_START_TOKEN = Token.of(InterpreterTestHelper.defaultTokenPairs().block().open()).build();

    @Test
    void testIfStatementEvaluatesIfConditionIsTrue() {
        boolean literalValue = true;
        Assertions.assertTrue(InterpreterUtilities.isTruthy(literalValue));

        Token conditionToken = Token.of(LiteralTokenType.TRUE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, true);

        this.assertEvaluatesToTrue(conditionExpression);
    }

    @Test
    void testIfStatementEvaluatesIfConditionIsTruthy() {
        int literalValue = 1;
        Assertions.assertTrue(InterpreterUtilities.isTruthy(literalValue));

        Token conditionToken = Token.of(LiteralTokenType.NUMBER).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, literalValue);

        this.assertEvaluatesToTrue(conditionExpression);
    }

    @Test
    void testIfStatementDoesNotEvaluateIfConditionIsFalse() {
        boolean literalValue = false;
        Assertions.assertFalse(InterpreterUtilities.isTruthy(literalValue));

        Token conditionToken = Token.of(LiteralTokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, false);

        this.assertEvaluatesToFalse(conditionExpression);
    }

    @Test
    void testIfStatementDoesNotEvaluateIfConditionIsFalsy() {
        Object literalValue = null;
        Assertions.assertFalse(InterpreterUtilities.isTruthy(null));

        Token conditionToken = Token.of(LiteralTokenType.NUMBER).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, literalValue);

        this.assertEvaluatesToFalse(conditionExpression);
    }

    @Test
    void testIfStatementDoesNotEvaluateOrFailIfElseBranchAbsent() {
        ExecutionCheckStatement trueExecutionCheck = new ExecutionCheckStatement();
        BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        boolean literalValue = false;
        Assertions.assertFalse(InterpreterUtilities.isTruthy(literalValue));

        Token conditionToken = Token.of(LiteralTokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, false);

        IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, null);
        ASTNodeInterpreter<Void, IfStatement> statementInterpreter = new IfStatementInterpreter();
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        Assertions.assertDoesNotThrow(() -> statementInterpreter.interpret(ifStatement, interpreter));

        Assertions.assertFalse(trueExecutionCheck.executed());
    }

    @Test
    void testIfStatementUpdatesScopeInThenBranch() {
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        AtomicBoolean executed = new AtomicBoolean(false);
        JavaStatement trueExecutionCheck = new JavaStatement(visitor -> {
            executed.set(true);
            Assertions.assertInstanceOf(DelegatingInterpreterVisitor.class, visitor);

            DelegatingInterpreterVisitor interpreterVisitor = (DelegatingInterpreterVisitor) visitor;
            Interpreter visitorInterpreter = interpreterVisitor.interpreter();
            VariableScope currentScope = visitorInterpreter.visitingScope();
            VariableScope globalScope = visitorInterpreter.global();
            Assertions.assertNotSame(globalScope, currentScope);
        });
        BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        boolean literalValue = true;
        Assertions.assertTrue(InterpreterUtilities.isTruthy(literalValue));

        Token conditionToken = Token.of(LiteralTokenType.TRUE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, true);

        IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, null);
        ASTNodeInterpreter<Void, IfStatement> statementInterpreter = new IfStatementInterpreter();
        statementInterpreter.interpret(ifStatement, interpreter);
        Assertions.assertTrue(executed.get());
    }

    @Test
    void testIfStatementUpdatesScopeInElseBranch() {
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        AtomicBoolean executed = new AtomicBoolean(false);
        JavaStatement falseExecutionCheck = new JavaStatement(visitor -> {
            executed.set(true);
            Assertions.assertTrue(visitor instanceof DelegatingInterpreterVisitor);

            DelegatingInterpreterVisitor interpreterVisitor = (DelegatingInterpreterVisitor) visitor;
            Interpreter visitorInterpreter = interpreterVisitor.interpreter();
            VariableScope currentScope = visitorInterpreter.visitingScope();
            VariableScope globalScope = visitorInterpreter.global();
            Assertions.assertNotSame(globalScope, currentScope);
        });
        BlockStatement ifFalse = new BlockStatement(BODY_START_TOKEN, List.of(falseExecutionCheck));

        ExecutionCheckStatement trueExecutionCheck = new ExecutionCheckStatement();
        BlockStatement ifTrue = new BlockStatement(BODY_START_TOKEN, List.of(trueExecutionCheck));

        boolean literalValue = false;
        Assertions.assertFalse(InterpreterUtilities.isTruthy(literalValue));

        Token conditionToken = Token.of(LiteralTokenType.FALSE).lexeme(String.valueOf(literalValue)).build();
        Expression conditionExpression = new LiteralExpression(conditionToken, false);

        IfStatement ifStatement = new IfStatement(conditionExpression, ifTrue, ifFalse);
        ASTNodeInterpreter<Void, IfStatement> statementInterpreter = new IfStatementInterpreter();
        statementInterpreter.interpret(ifStatement, interpreter);
        Assertions.assertFalse(trueExecutionCheck.executed());
        Assertions.assertTrue(executed.get());
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

        Assertions.assertEquals(evaluatesTo, trueExecutionCheck.executed());
        Assertions.assertNotEquals(evaluatesTo, falseExecutionCheck.executed());
    }
}
