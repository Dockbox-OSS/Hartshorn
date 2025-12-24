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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArrayComprehensionExpressionInterpreter;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryAdditionExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.ComplexArrayExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
public class ArrayComprehensionExpressionTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> arrayInputs() {
        return Stream.of(
            Arguments.of(new Array(new Object[] {"test"})),
            Arguments.of(List.of("test")),
            // Note: first array is captured as varargs. Inner array
            // is the actual input array.
            Arguments.of(new Object[] {new Object[] {"test"}})
        );
    }

    @ParameterizedTest
    @MethodSource("arrayInputs")
    void arrayComprehensionWithoutTransformation(Object inputArray) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(this.applicationContext, """
                [x for x in list]
                """)
            .expressionParser(new IdentifierExpressionParser())
            .expressionParser(new ComplexArrayExpressionParser())
            .defineLocal("list", inputArray)
            .build();

        Object interpreted = helper.evaluateWith(
            ArrayComprehensionExpression.class,
            new ArrayComprehensionExpressionInterpreter()
        ).interpretValue();
        Array array = assertThat(interpreted)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.length()).isOne();
        assertThat(array.value(0)).isEqualTo("test");
    }

    @ParameterizedTest
    @MethodSource("arrayInputs")
    void arrayComprehensionWithBasicTransformation(Object inputArray) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(this.applicationContext, """
                [x + "2" for x in list]
                """)
            .expressionParser(new BinaryAdditionExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new ComplexArrayExpressionParser())
            .defineLocal("list", inputArray)
            .build();

        Object interpreted = helper.evaluateWith(
            ArrayComprehensionExpression.class,
            new ArrayComprehensionExpressionInterpreter()
        ).interpretValue();
        Array array = assertThat(interpreted)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.length()).isOne();
        assertThat(array.value(0)).isEqualTo("test2");
    }

    @ParameterizedTest
    @MethodSource("arrayInputs")
    void arrayComprehensionWithConditional(Object inputArray) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(this.applicationContext, """
                [x for x in list if false]
                """)
            .expressionParser(new IdentifierExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new ComplexArrayExpressionParser())
            .defineLocal("list", inputArray)
            .build();

        Object interpreted = helper.evaluateWith(
            ArrayComprehensionExpression.class,
            new ArrayComprehensionExpressionInterpreter()
        ).interpretValue();
        Array array = assertThat(interpreted)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.length()).isZero();
    }

    @ParameterizedTest
    @MethodSource("arrayInputs")
    void arrayComprehensionWithConditionalAlternative(Object inputArray) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(this.applicationContext, """
                [x for x in list if false else "other"]
                """)
            .expressionParser(new IdentifierExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new ComplexArrayExpressionParser())
            .defineLocal("list", inputArray)
            .build();

        Object interpreted = helper.evaluateWith(
            ArrayComprehensionExpression.class,
            new ArrayComprehensionExpressionInterpreter()
        ).interpretValue();
        Array array = assertThat(interpreted)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.length()).isOne();
        assertThat(array.value(0)).isEqualTo("other");
    }
}