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

import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.CacheOnlyResultCollector;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArraySetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ArraySetExpressionInterpreterTests {

    @Test
    void testSetWithinArrayRange() {
        final ResultCollector resultCollector = new CacheOnlyResultCollector();
        final InterpreterAdapter interpreter = new Interpreter(resultCollector, Map.of(), null);

        final Object[] realArray = { "test" };
        final int targetIndex = 0;

        final Token indexToken = Token.of(TokenType.NUMBER).literal(targetIndex).build();
        final LiteralExpression index = new LiteralExpression(indexToken, targetIndex);

        final Token arrayIdentifier = Token.of(TokenType.IDENTIFIER).lexeme("test").build();
        final Array hslArray = new Array(realArray);
        interpreter.visitingScope().define(arrayIdentifier.lexeme(), hslArray);

        final Token valueToken = Token.of(TokenType.STRING).literal("value").build();
        final LiteralExpression literalExpression = new LiteralExpression(valueToken, "value");

        final ASTNodeInterpreter<Object, ArraySetExpression> expressionInterpreter = new ArraySetExpressionInterpreter();
        final ArraySetExpression setExpression = new ArraySetExpression(arrayIdentifier, index, literalExpression);

        final Object interpreted = expressionInterpreter.interpret(setExpression, interpreter);
        Assertions.assertEquals(literalExpression.value(), interpreted);
        Assertions.assertEquals(literalExpression.value(), hslArray.value(targetIndex));
    }

    @Test
    void testSetOutsideRangeThrowsOutOfBounds() {
        final ResultCollector resultCollector = new CacheOnlyResultCollector();
        final InterpreterAdapter interpreter = new Interpreter(resultCollector, Map.of(), null);

        final Object[] realArray = { "test" };
        final int targetIndex = 1;

        final Token indexToken = Token.of(TokenType.NUMBER).literal(targetIndex).build();
        final LiteralExpression index = new LiteralExpression(indexToken, targetIndex);

        final Token arrayIdentifier = Token.of(TokenType.IDENTIFIER).lexeme("test").build();
        final Array hslArray = new Array(realArray);
        interpreter.visitingScope().define(arrayIdentifier.lexeme(), hslArray);

        final Token valueToken = Token.of(TokenType.STRING).literal("value").build();
        final LiteralExpression literalExpression = new LiteralExpression(valueToken, "value");

        final ASTNodeInterpreter<Object, ArraySetExpression> expressionInterpreter = new ArraySetExpressionInterpreter();
        final ArraySetExpression setExpression = new ArraySetExpression(arrayIdentifier, index, literalExpression);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> expressionInterpreter.interpret(setExpression, interpreter));
    }
}