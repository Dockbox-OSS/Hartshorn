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
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArraySetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class ArraySetExpressionInterpreterTests {

    @Test
    void testSetWithinArrayRange() {
        Object[] realArray = { "test" };
        int targetIndex = 0;

        Token indexToken = Token.of(LiteralTokenType.NUMBER).literal(targetIndex).build();
        LiteralExpression index = new LiteralExpression(indexToken, targetIndex);

        Token arrayIdentifier = Token.of(LiteralTokenType.IDENTIFIER).lexeme("test").build();
        Array hslArray = new Array(realArray);
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        interpreter.visitingScope().define(arrayIdentifier.lexeme(), hslArray);

        Token valueToken = Token.of(LiteralTokenType.STRING).literal("value").build();
        LiteralExpression literalExpression = new LiteralExpression(valueToken, "value");

        ASTNodeInterpreter<Object, ArraySetExpression> expressionInterpreter = new ArraySetExpressionInterpreter();
        ArraySetExpression setExpression = new ArraySetExpression(arrayIdentifier, index, literalExpression);

        Object interpreted = expressionInterpreter.interpret(setExpression, interpreter);
        Assertions.assertEquals(literalExpression.value(), interpreted);
        Assertions.assertEquals(literalExpression.value(), hslArray.value(targetIndex));
    }

    @Test
    void testSetOutsideRangeThrowsOutOfBounds() {
        Object[] realArray = { "test" };
        int targetIndex = 1;

        Token indexToken = Token.of(LiteralTokenType.NUMBER).literal(targetIndex).build();
        LiteralExpression index = new LiteralExpression(indexToken, targetIndex);

        Token arrayIdentifier = Token.of(LiteralTokenType.IDENTIFIER).lexeme("test").build();
        Array hslArray = new Array(realArray);
        Interpreter interpreter = InterpreterTestHelper.createInterpreter();
        interpreter.visitingScope().define(arrayIdentifier.lexeme(), hslArray);

        Token valueToken = Token.of(LiteralTokenType.STRING).literal("value").build();
        LiteralExpression literalExpression = new LiteralExpression(valueToken, "value");

        ASTNodeInterpreter<Object, ArraySetExpression> expressionInterpreter = new ArraySetExpressionInterpreter();
        ArraySetExpression setExpression = new ArraySetExpression(arrayIdentifier, index, literalExpression);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> expressionInterpreter.interpret(setExpression, interpreter));
    }
}