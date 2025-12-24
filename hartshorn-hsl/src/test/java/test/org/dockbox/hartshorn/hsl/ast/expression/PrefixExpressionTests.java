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
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.parser.expression.FunctionParserContext;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.PrefixExpressionParser;
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
class PrefixExpressionTests {

    @Test
    void prefixExpressionWithDefinitionAndImplementationPasses(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "not true")
            .expressionParser(new PrefixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                // Prefix function definition
                FunctionParserContext functionParserContext = new FunctionParserContext();
                functionParserContext.addPrefixFunction("not");
                parser.addContext(functionParserContext);
            })
            // Prefix function implementation
            .defineLocal("not", (CallableNode) (at, interpreter, instance, arguments) -> !((Boolean) arguments.getFirst()))
            .build();

        Object value = helper.interpretValue();
        assertThat(value)
                .asInstanceOf(InstanceOfAssertFactories.BOOLEAN)
                .isFalse();
    }

    @Test
    void prefixExpressionWithoutDefinitionFailsAtParser(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "not true")
            .expressionParser(new PrefixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                FunctionParserContext functionParserContext = new FunctionParserContext();
                parser.addContext(functionParserContext);
            })
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::expression).actual();
        assertThat(error.phase()).isEqualTo(Phase.PARSING);
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.EXPECTED_EXPRESSION);
    }

    @Test
    void prefixExpressionWithDefinitionAndWithoutImplementationFailsAtInterpreter(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "not true")
            .expressionParser(new PrefixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                FunctionParserContext functionParserContext = new FunctionParserContext();
                functionParserContext.addPrefixFunction("not");
                parser.addContext(functionParserContext);
            })
            .build();

        helper.parse();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::interpretValue).actual();
        assertThat(error.phase()).isEqualTo(Phase.INTERPRETING);
        ScriptAssertions.assertEvaluationError(
            error,
            FormattedDiagnostic.of(DiagnosticMessage.UNDEFINED_VARIABLE, "not")
        );
    }

    @Test
    void prefixExpressionWithoutArgumentFailsAtParser(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "not")
            .expressionParser(new PrefixExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .parser(parser -> {
                FunctionParserContext functionParserContext = new FunctionParserContext();
                functionParserContext.addPrefixFunction("not");
                parser.addContext(functionParserContext);
            })
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::expression).actual();
        assertThat(error.phase()).isEqualTo(Phase.PARSING);
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.EXPECTED_EXPRESSION);
    }
}