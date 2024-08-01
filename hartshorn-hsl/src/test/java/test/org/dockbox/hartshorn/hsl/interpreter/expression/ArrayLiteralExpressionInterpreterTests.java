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

package test.org.dockbox.hartshorn.hsl.interpreter.expression;

import java.util.List;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArrayLiteralExpressionInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class ArrayLiteralExpressionInterpreterTests {

    @Test
    void testEmptyArrayLiteralYieldsEmptyArrayObject() {
        ArrayLiteralExpression expression = createExpression(List.of());

        Object interpreted = InterpreterTestHelper.interpret(expression, new ArrayLiteralExpressionInterpreter());
        Assertions.assertNotNull(interpreted);
        Assertions.assertTrue(interpreted instanceof Array);

        Array array = (Array) interpreted;
        Assertions.assertEquals(0, array.length());
    }

    @Test
    void testArrayLiteralYieldsArrayObject() {
        Token value = Token.of(LiteralTokenType.STRING).literal("test").build();
        LiteralExpression literalExpression = new LiteralExpression(value, value.literal());
        ArrayLiteralExpression expression = createExpression(List.of(literalExpression));

        Object interpreted = InterpreterTestHelper.interpret(expression, new ArrayLiteralExpressionInterpreter());
        Assertions.assertNotNull(interpreted);
        Assertions.assertTrue(interpreted instanceof Array);

        Array array = (Array) interpreted;
        Assertions.assertEquals(1, array.length());
        Assertions.assertEquals("test", array.value(0));
    }

    private static ArrayLiteralExpression createExpression(List<Expression> expressions) {
        Token open = Token.of(InterpreterTestHelper.defaultTokenPairs().array().open()).build();
        Token close = Token.of(InterpreterTestHelper.defaultTokenPairs().array().close()).build();
        return new ArrayLiteralExpression(open, close, expressions);
    }
}
