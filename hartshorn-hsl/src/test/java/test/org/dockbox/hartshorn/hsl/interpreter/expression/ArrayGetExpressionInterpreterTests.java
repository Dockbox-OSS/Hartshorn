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

import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArrayGetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class ArrayGetExpressionInterpreterTests {

    @Test
    void testArrayGetExpressionCanGetIfInRange() {
        final Object[] realArray = { "test" };
        final int targetIndex = 0;

        final Token indexToken = Token.of(TokenType.NUMBER).literal(targetIndex).build();
        final LiteralExpression index = new LiteralExpression(indexToken, targetIndex);

        final Token arrayIdentifier = Token.of(TokenType.IDENTIFIER).lexeme("test").build();
        final InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        adapter.visitingScope().define(arrayIdentifier.lexeme(), new Array(realArray));

        final ASTNodeInterpreter<Object, ArrayGetExpression> expressionInterpreter = new ArrayGetExpressionInterpreter();
        final ArrayGetExpression getExpression = new ArrayGetExpression(arrayIdentifier, index);

        final Object interpretedValue = expressionInterpreter.interpret(getExpression, adapter);
        Assertions.assertEquals(realArray[targetIndex], interpretedValue);
    }

    @Test
    void testArrayGetExpressionThrowsIfOutOfRange() {
        final Object[] realArray = { "test" };
        final int targetIndex = 1;

        final Token indexToken = Token.of(TokenType.NUMBER).literal(targetIndex).build();
        final LiteralExpression index = new LiteralExpression(indexToken, targetIndex);

        final Token arrayIdentifier = Token.of(TokenType.IDENTIFIER).lexeme("test").build();
        final InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        adapter.visitingScope().define(arrayIdentifier.lexeme(), new Array(realArray));

        final ASTNodeInterpreter<Object, ArrayGetExpression> expressionInterpreter = new ArrayGetExpressionInterpreter();
        final ArrayGetExpression getExpression = new ArrayGetExpression(arrayIdentifier, index);

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> expressionInterpreter.interpret(getExpression, adapter));
    }
}