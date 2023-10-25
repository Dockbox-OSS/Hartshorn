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
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArraySetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class ArraySetExpressionInterpreterTests {

    @Test
    void testSetWithinArrayRange() {
        Object[] realArray = { "test" };
        int targetIndex = 0;

        Token indexToken = Token.of(TokenType.NUMBER).literal(targetIndex).build();
        LiteralExpression index = new LiteralExpression(indexToken, targetIndex);

        Token arrayIdentifier = Token.of(TokenType.IDENTIFIER).lexeme("test").build();
        Array hslArray = new Array(realArray);
        InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        adapter.visitingScope().define(arrayIdentifier.lexeme(), hslArray);

        Token valueToken = Token.of(TokenType.STRING).literal("value").build();
        LiteralExpression literalExpression = new LiteralExpression(valueToken, "value");

        ASTNodeInterpreter<Object, ArraySetExpression> expressionInterpreter = new ArraySetExpressionInterpreter();
        ArraySetExpression setExpression = new ArraySetExpression(arrayIdentifier, index, literalExpression);

        Object interpreted = expressionInterpreter.interpret(setExpression, adapter);
        Assertions.assertEquals(literalExpression.value(), interpreted);
        Assertions.assertEquals(literalExpression.value(), hslArray.value(targetIndex));
    }

    @Test
    void testSetOutsideRangeThrowsOutOfBounds() {
        Object[] realArray = { "test" };
        int targetIndex = 1;

        Token indexToken = Token.of(TokenType.NUMBER).literal(targetIndex).build();
        LiteralExpression index = new LiteralExpression(indexToken, targetIndex);

        Token arrayIdentifier = Token.of(TokenType.IDENTIFIER).lexeme("test").build();
        Array hslArray = new Array(realArray);
        InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        adapter.visitingScope().define(arrayIdentifier.lexeme(), hslArray);

        Token valueToken = Token.of(TokenType.STRING).literal("value").build();
        LiteralExpression literalExpression = new LiteralExpression(valueToken, "value");

        ASTNodeInterpreter<Object, ArraySetExpression> expressionInterpreter = new ArraySetExpressionInterpreter();
        ArraySetExpression setExpression = new ArraySetExpression(arrayIdentifier, index, literalExpression);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> expressionInterpreter.interpret(setExpression, adapter));
    }
}