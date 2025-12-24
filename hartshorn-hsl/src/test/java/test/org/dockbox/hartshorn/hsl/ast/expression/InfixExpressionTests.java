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
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.parser.expression.FunctionParserContext;
import org.dockbox.hartshorn.hsl.parser.expression.InfixExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class InfixExpressionTests {

    @Test
    void infixExpressionWithDefinitionAndImplementationPasses(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, """
                "pizza" or "pasta"
                """)
            .expressionParser(new InfixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                // Infix function definition
                FunctionParserContext functionParserContext = new FunctionParserContext();
                functionParserContext.addInfixFunction("or");
                parser.addContext(functionParserContext);
            })
            // Infix function implementation
            .defineLocal("or", (CallableNode) (_, _, _, arguments) -> {
                Object left = arguments.getFirst();
                Object right = arguments.getLast();
                return InterpreterUtilities.isTruthy(left) ? left : right;
            })
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo("pizza");
    }

    @Test
    void infixExpressionWithoutDefinitionFailsAtParser(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(
                applicationContext,
                "\"pizza\" or \"pasta\""
            )
            .expressionParser(new InfixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                FunctionParserContext functionParserContext = new FunctionParserContext();
                // No infix function definition added
                parser.addContext(functionParserContext);
            })
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::expression).actual();
        assertThat(error.phase()).isEqualTo(Phase.PARSING);
    }

    @Test
    void infixExpressionWithDefinitionAndWithoutImplementationFailsAtInterpreter(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, """
                "pizza" or "pasta"
                """)
            .expressionParser(new InfixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                FunctionParserContext functionParserContext = new FunctionParserContext();
                functionParserContext.addInfixFunction("or");
                parser.addContext(functionParserContext);
            })
            .build();

        helper.parse();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::interpretValue).actual();
        assertThat(error.phase()).isEqualTo(Phase.INTERPRETING);
        ScriptAssertions.assertEvaluationError(
            error,
            FormattedDiagnostic.of(DiagnosticMessage.UNDEFINED_VARIABLE, "or")
        );
    }

    @Test
    void infixExpressionWithoutLeftArgumentFailsAtParser(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "or true")
            .expressionParser(new InfixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                FunctionParserContext functionParserContext = new FunctionParserContext();
                functionParserContext.addInfixFunction("or");
                parser.addContext(functionParserContext);
            })
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::expression).actual();
        assertThat(error.phase()).isEqualTo(Phase.PARSING);
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.EXPECTED_EXPRESSION);
    }

    @Test
    void infixExpressionWithoutRightArgumentFailsAtParser(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "true or")
            .expressionParser(new InfixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                FunctionParserContext functionParserContext = new FunctionParserContext();
                functionParserContext.addInfixFunction("or");
                parser.addContext(functionParserContext);
            })
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::expression).actual();
        assertThat(error.phase()).isEqualTo(Phase.PARSING);
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.EXPECTED_EXPRESSION);
    }
}