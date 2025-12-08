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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.interpreter.expression.AssignExpressionInterpreter;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.parser.expression.AssignExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import java.util.function.Function;
import java.util.stream.Stream;

@HartshornIntegrationTest(includeBasePackages = false)
public class AssignExpressionTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> variableDefinitionScopes() {
        return Stream.of(
                Arguments.of((Function<Interpreter, VariableScope>) Interpreter::visitingScope),
                Arguments.of((Function<Interpreter, VariableScope>) Interpreter::global)
        );
    }

    @ParameterizedTest
    @MethodSource("variableDefinitionScopes")
    void testAssignmentToDefinedVariable(
            Function<Interpreter, VariableScope> variableScopeFunction
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(this.applicationContext, """
                        variable = "newValue"
                        """)
                .expressionParser(new AssignExpressionParser())
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .define("variable", "originalValue", variableScopeFunction)
                .build();

        Object interpreted = helper
                .evaluateWith(AssignExpression.class, new AssignExpressionInterpreter())
                .interpretValue();
        Assertions.assertEquals("newValue", interpreted);

        VariableScope scope = variableScopeFunction.apply(helper.interpreter());
        Object variableValue = scope.get(Token.of(LiteralTokenType.IDENTIFIER, "variable").build());
        Assertions.assertEquals("newValue", variableValue);
    }

    @Test
    void assignmentToPropertyContainer() {
        PropertyContainer container = Mockito.mock(PropertyContainer.class);
        Mockito.doAnswer(invocation -> {
            Object value = invocation.getArgument(2);
            Assertions.assertEquals(42d, value);
            return null;
        }).when(container).set(
                Mockito.any(Interpreter.class),
                Mockito.any(Token.class),
                Mockito.any(Object.class),
                Mockito.any(VariableScope.class)
        );

        HSLTestHelper helper = HSLTestHelper.ofExpression(this.applicationContext, "object.property = 42")
                .expressionParser(new AssignExpressionParser())
                .expressionParser(new CallExpressionParser())
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .defineLocal("object", container)
                .build();

        Object value = helper.interpretValue();
        Assertions.assertEquals(42d, value);

        Mockito.verify(container).set(
                Mockito.eq(helper.interpreter()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        );
    }

    @Test
    void testAssignmentToUndefinedVariable() {
        HSLTestHelper helper = HSLTestHelper.ofExpression(this.applicationContext, """
                        variable = "newValue"
                        """)
                .expressionParser(new AssignExpressionParser())
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .build();

        Assertions.assertThrows(ScriptEvaluationError.class, () -> helper
                .evaluateWith(AssignExpression.class, new AssignExpressionInterpreter())
                .interpretValue());
    }
}
