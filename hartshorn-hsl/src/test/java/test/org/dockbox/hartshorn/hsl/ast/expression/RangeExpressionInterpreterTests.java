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

import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.RangeExpressionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.ast.HSLTestHelper;

public class RangeExpressionInterpreterTests {

    @Test
    void rangeYieldsLeftRightInclusiveArray() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("1..5")
                .expressionParser(new RangeExpressionParser())
                .expressionParser(new LiteralExpressionParser());

        Object value = helper.interpretValue();
        Array array = Assertions.assertInstanceOf(Array.class, value);
        Assertions.assertArrayEquals(
                new Double[]{1d, 2d, 3d, 4d, 5d},
                array.values()
        );
    }

    @Test
    void rangeWithSameValueYieldsSingleElementArray() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("2..2")
                .expressionParser(new RangeExpressionParser())
                .expressionParser(new LiteralExpressionParser());

        Object value = helper.interpretValue();
        Array array = Assertions.assertInstanceOf(Array.class, value);
        Assertions.assertArrayEquals(
                new Double[] { 2d },
                array.values()
        );
    }

    @Test
    void rangeSupportsIdentifierValues() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("min..max")
                .expressionParser(new RangeExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .expressionParser(new LiteralExpressionParser());

        helper.defineVariable("min", 1);
        helper.defineVariable("max", 5);

        Object value = helper.interpretValue();
        Array array = Assertions.assertInstanceOf(Array.class, value);
        Assertions.assertArrayEquals(
                new Double[] { 1d, 2d, 3d, 4d, 5d },
                array.values()
        );
    }
}