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

package test.org.dockbox.hartshorn.hsl.ast.statement;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.parser.expression.FunctionParserContext;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.FunctionStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ReturnStatementParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.HartshornAssertions;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class FunctionStatementInterpreterTests {

    @Test
    void regularFunctionWithoutExplicitReturnDeclarationIsDefined(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, "function greet(name) { }")
            .statementParser(new FunctionStatementParser())
            .statementParser(new BlockStatementParser())
            .build();

        helper.interpret();

        Object greet = helper.findVariable("greet");
        VirtualFunction greetFunction = assertThat(greet)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualFunction.class))
                .actual();
        assertThat(greetFunction.returnType()).isEqualTo(ReturnStatement.ReturnType.RETURN);

        FunctionStatement functionStatement = assertThat(greetFunction.declaration())
                .asInstanceOf(InstanceOfAssertFactories.type(FunctionStatement.class))
                .actual();
        assertThat(functionStatement.name().lexeme()).isEqualTo("greet");
        assertThat(functionStatement.parameters()).hasSize(1);
        assertThat(functionStatement.parameters().getFirst().name().lexeme()).isEqualTo("name");
    }

    @Test
    void regularFunctionWithExplicitReturnDeclarationIsDefined(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(
                applicationContext,
                "function greet(name) { return null; }"
            )
            .statementParser(new FunctionStatementParser())
            .statementParser(new BlockStatementParser())
            .statementParser(new ReturnStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();

        Object greet = helper.findVariable("greet");
        VirtualFunction greetFunction = assertThat(greet)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualFunction.class))
                .actual();
        assertThat(greetFunction.returnType()).isEqualTo(ReturnStatement.ReturnType.RETURN);

        FunctionStatement functionStatement = assertThat(greetFunction.declaration())
                .asInstanceOf(InstanceOfAssertFactories.type(FunctionStatement.class))
                .actual();
        assertThat(functionStatement.name().lexeme()).isEqualTo("greet");
        assertThat(functionStatement.parameters()).hasSize(1);
        assertThat(functionStatement.parameters().getFirst().name().lexeme()).isEqualTo("name");
    }

    @Test
    void prefixFunctionDeclarationWithOneParameterIsDefined(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper =
            HSLTestHelper.of(applicationContext, "prefix function greet(name) { }")
                .statementParser(new FunctionStatementParser())
                .statementParser(new BlockStatementParser())
                .build();

        helper.interpret();

        Object greet = helper.findVariable("greet");
        VirtualFunction greetFunction = assertThat(greet)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualFunction.class))
                .actual();
        assertThat(greetFunction.returnType()).isEqualTo(ReturnStatement.ReturnType.RETURN);
        FunctionStatement functionStatement = assertThat(greetFunction.declaration())
                .asInstanceOf(InstanceOfAssertFactories.type(FunctionStatement.class))
                .actual();
        assertThat(functionStatement.name().lexeme()).isEqualTo("greet");
        assertThat(functionStatement.parameters()).hasSize(1);
        assertThat(functionStatement.parameters().getFirst().name().lexeme()).isEqualTo("name");

        Option<FunctionParserContext> functionParserContext = helper.context()
                .parser()
                .firstContext(FunctionParserContext.class);

        HartshornAssertions.assertThat(functionParserContext)
                .value()
                .extracting(
                        FunctionParserContext::prefixFunctions,
                        InstanceOfAssertFactories.collection(String.class)
                ).contains("greet");
    }

    @Test
    void prefixFunctionDeclarationWithMultipleParametersFails(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(
                applicationContext,
                "prefix function greet(name, other) { }"
            )
            .statementParser(new FunctionStatementParser())
            .statementParser(new BlockStatementParser())
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TOO_MANY_PARAMETERS_FOR_X);
    }

    @Test
    void prefixFunctionDeclarationWithZeroParametersFails(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(
                applicationContext,
                "prefix function greet() { }"
            )
            .statementParser(new FunctionStatementParser())
            .statementParser(new BlockStatementParser())
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::interpret).actual();
        ScriptAssertions.assertEvaluationError(error,
            DiagnosticMessage.NOT_ENOUGH_PARAMETERS_FOR_X);
    }

    @Test
    void infixFunctionDeclarationWithTwoParametersIsDefined(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper =
            HSLTestHelper.of(applicationContext, "infix function combine(a, b) { }")
                .statementParser(new FunctionStatementParser())
                .statementParser(new BlockStatementParser())
                .build();
        helper.interpret();

        Object combine = helper.findVariable("combine");
        VirtualFunction combineFunction = assertThat(combine)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualFunction.class))
                .actual();
        assertThat(combineFunction.returnType()).isEqualTo(ReturnStatement.ReturnType.RETURN);
        FunctionStatement functionStatement = assertThat(combineFunction.declaration())
                .asInstanceOf(InstanceOfAssertFactories.type(FunctionStatement.class))
                .actual();
        assertThat(functionStatement.name().lexeme()).isEqualTo("combine");
        assertThat(functionStatement.parameters()).hasSize(2);
        assertThat(functionStatement.parameters().getFirst().name().lexeme()).isEqualTo("a");
        assertThat(functionStatement.parameters().getLast().name().lexeme()).isEqualTo("b");

        Option<FunctionParserContext> functionParserContext = helper.context().parser()
            .firstContext(FunctionParserContext.class);
        assertThat(functionParserContext.present()).isTrue();
        FunctionParserContext context = functionParserContext.get();
        assertThat(context.infixFunctions()).contains("combine");
    }

    @Test
    void infixFunctionDeclarationWithTooManyParametersFails(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(
                applicationContext,
                "infix function combine(a, b, c) { }"
            )
            .statementParser(new FunctionStatementParser())
            .statementParser(new BlockStatementParser())
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TOO_MANY_PARAMETERS_FOR_X);
    }

    @Test
    void infixFunctionDeclarationWithTooFewParametersFails(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(
                applicationContext,
                "infix function combine(a) { }"
            )
            .statementParser(new FunctionStatementParser())
            .statementParser(new BlockStatementParser())
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::interpret).actual();
        ScriptAssertions.assertEvaluationError(error,
            DiagnosticMessage.NOT_ENOUGH_PARAMETERS_FOR_X);
    }
}