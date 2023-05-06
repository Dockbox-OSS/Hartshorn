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

import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArrayLiteralExpressionInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class ArrayLiteralExpressionInterpreterTests {

    @Test
    void testEmptyArrayLiteralYieldsEmptyArrayObject() {
        final ArrayLiteralExpression expression = createExpression(List.of());

        final Object interpreted = InterpreterTestHelper.interpret(expression, new ArrayLiteralExpressionInterpreter());
        Assertions.assertNotNull(interpreted);
        Assertions.assertTrue(interpreted instanceof Array);

        final Array array = (Array) interpreted;
        Assertions.assertEquals(0, array.length());
    }

    @Test
    void testArrayLiteralYieldsArrayObject() {
        final Token value = Token.of(TokenType.STRING).literal("test").build();
        final LiteralExpression literalExpression = new LiteralExpression(value, value.literal());
        final ArrayLiteralExpression expression = createExpression(List.of(literalExpression));

        final Object interpreted = InterpreterTestHelper.interpret(expression, new ArrayLiteralExpressionInterpreter());
        Assertions.assertNotNull(interpreted);
        Assertions.assertTrue(interpreted instanceof Array);

        final Array array = (Array) interpreted;
        Assertions.assertEquals(1, array.length());
        Assertions.assertEquals("test", array.value(0));
    }

    @NotNull
    private static ArrayLiteralExpression createExpression(final List<Expression> expressions) {
        final Token open = Token.of(TokenType.ARRAY_OPEN).lexeme("[").build();
        final Token close = Token.of(TokenType.ARRAY_CLOSE).lexeme("]").build();
        return new ArrayLiteralExpression(open, close, expressions);
    }
}