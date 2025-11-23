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
import org.dockbox.hartshorn.hsl.parser.expression.ComplexArrayExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.ast.HSLTestHelper;

public class ArrayLiteralExpressionInterpreterTests {

    @Test
    void testEmptyArrayLiteralYieldsEmptyArrayObject() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("[]")
                .expressionParser(new ComplexArrayExpressionParser());
        Object value = helper.interpretValue();

        Array array = Assertions.assertInstanceOf(Array.class, value);
        Assertions.assertEquals(0, array.length());
    }

    @Test
    void testSingleValueArrayLiteralYieldsArrayObject() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("[\"test\"]")
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new ComplexArrayExpressionParser());
        Object value = helper.interpretValue();

        Array array = Assertions.assertInstanceOf(Array.class, value);
        Assertions.assertEquals(1, array.length());
        Assertions.assertEquals("test", array.value(0));
    }

    @Test
    void testMultipleValueArrayLiteralYieldsArrayObject() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("[\"test\", \"test2\"]")
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new ComplexArrayExpressionParser());
        Object value = helper.interpretValue();

        Array array = Assertions.assertInstanceOf(Array.class, value);
        Assertions.assertEquals(2, array.length());
        Assertions.assertEquals("test", array.value(0));
        Assertions.assertEquals("test2", array.value(1));
    }
}
