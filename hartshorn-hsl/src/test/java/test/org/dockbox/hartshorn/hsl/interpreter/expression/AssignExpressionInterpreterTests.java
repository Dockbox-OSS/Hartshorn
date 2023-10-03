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

import java.util.function.Function;
import java.util.stream.Stream;

import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.interpreter.expression.AssignExpressionInterpreter;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class AssignExpressionInterpreterTests {

    public static Stream<Arguments> variableDefinitionScopes() {
        return Stream.of(
                Arguments.of((Function<InterpreterAdapter, VariableScope>) InterpreterAdapter::visitingScope),
                Arguments.of((Function<InterpreterAdapter, VariableScope>) InterpreterAdapter::global)
        );
    }

    @ParameterizedTest
    @MethodSource("variableDefinitionScopes")
    void testAssignmentToDefinedVariable(final Function<InterpreterAdapter, VariableScope> variableScopeFunction) {
        Token variableName = Token.of(LiteralTokenType.IDENTIFIER)
                .lexeme("test")
                .build();

        Token variableValue = Token.of(LiteralTokenType.STRING)
                .literal("theValue")
                .build();

        LiteralExpression literalExpression = new LiteralExpression(variableValue, variableValue.literal());
        AssignExpression expression = new AssignExpression(variableName, literalExpression);
        ASTNodeInterpreter<Object, AssignExpression> interpreter = new AssignExpressionInterpreter();

        InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        variableScopeFunction.apply(adapter).define(variableName.lexeme(), "test");

        Object interpreted = Assertions.assertDoesNotThrow(() -> interpreter.interpret(expression, adapter));
        Assertions.assertEquals(literalExpression.value(), interpreted);
    }

    @Test
    void testAssignmentToUndefinedVariable() {
        Token variableName = Token.of(LiteralTokenType.IDENTIFIER)
                .lexeme("test")
                .build();

        Token variableValue = Token.of(LiteralTokenType.STRING)
                .literal("theValue")
                .build();

        LiteralExpression literalExpression = new LiteralExpression(variableValue, variableValue.literal());
        AssignExpression expression = new AssignExpression(variableName, literalExpression);
        ASTNodeInterpreter<Object, AssignExpression> interpreter = new AssignExpressionInterpreter();

        InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();

        RuntimeError error = Assertions.assertThrows(RuntimeError.class, () -> interpreter.interpret(expression, adapter));
        Assertions.assertSame(variableName, error.token());
    }
}