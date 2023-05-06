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

package test.org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.expression.AssignExpressionInterpreter;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class AssignExpressionInterpreterTests {

    @Test
    void testAssignmentToDefinedVariable() {
        final Token variableName = Token.of(TokenType.IDENTIFIER)
                .lexeme("test")
                .build();

        final Token variableValue = Token.of(TokenType.STRING)
                .literal("theValue")
                .build();

        final LiteralExpression literalExpression = new LiteralExpression(variableValue, variableValue.literal());
        final AssignExpression expression = new AssignExpression(variableName, literalExpression);
        final ASTNodeInterpreter<Object, AssignExpression> interpreter = new AssignExpressionInterpreter();

        final InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        adapter.visitingScope().define(variableName.lexeme(), "test");

        final Object interpreted = Assertions.assertDoesNotThrow(() -> interpreter.interpret(expression, adapter));
        Assertions.assertEquals(literalExpression.value(), interpreted);
    }

    @Test
    void testAssignmentToUndefinedVariable() {
        final Token variableName = Token.of(TokenType.IDENTIFIER)
                .lexeme("test")
                .build();

        final Token variableValue = Token.of(TokenType.STRING)
                .literal("theValue")
                .build();

        final LiteralExpression literalExpression = new LiteralExpression(variableValue, variableValue.literal());
        final AssignExpression expression = new AssignExpression(variableName, literalExpression);
        final ASTNodeInterpreter<Object, AssignExpression> interpreter = new AssignExpressionInterpreter();

        final InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();

        final RuntimeError error = Assertions.assertThrows(RuntimeError.class, () -> interpreter.interpret(expression, adapter));
        Assertions.assertSame(variableName, error.token());
    }
}