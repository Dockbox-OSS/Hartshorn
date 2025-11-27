/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArrayGetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.HSLTestHelper;

public class ArrayGetExpressionTests {

    @Test
    void testArrayGetExpressionCanGetIfInRange() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("array[0]")
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        Object[] realArray = {"test"};
        helper.defineVariable("array", new Array(realArray));

        Object interpretedValue = helper.interpret(
                ArrayGetExpression.class,
                new ArrayGetExpressionInterpreter()
        );
        Assertions.assertSame(realArray[0], interpretedValue);
    }

    @Test
    void testArrayGetExpressionThrowsIfOutOfRange() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("array[1]")
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        Object[] realArray = {"test"};
        helper.defineVariable("array", new Array(realArray));

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            helper.interpret(
                    ArrayGetExpression.class,
                    new ArrayGetExpressionInterpreter()
            );
        });
    }
}