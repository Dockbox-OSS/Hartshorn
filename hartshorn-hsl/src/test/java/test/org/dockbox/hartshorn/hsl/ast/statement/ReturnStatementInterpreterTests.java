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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.ReturnStatementParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.runtime.Yield;
import org.dockbox.hartshorn.hsl.semantic.FunctionType;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class ReturnStatementInterpreterTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void returnStatementThrowsCaptureExceptionForReturn() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, "return 42")
            .statementParser(new ReturnStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context ->
                // Cannot return from top-level code, so we mark the current context
                // as being inside a function
                context.resolver().currentFunction(FunctionType.INLINE_FUNCTION)))
            .build();

        Return returnCapture = assertThatExceptionOfType(Return.class).isThrownBy(helper::interpret).actual();
        assertThat(returnCapture.value()).isEqualTo(42d);
    }

    @Test
    void returnStatementThrowsCaptureExceptionForYield() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, "return 3.14")
            .statementParser(new ReturnStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context ->
                // Cannot return from top-level code, so we mark the current context
                // as being inside a generator function
                context.resolver().currentFunction(FunctionType.FIELD_MEMBER)))
            .build();

        Return returnCapture = assertThatExceptionOfType(Return.class).isThrownBy(helper::interpret).actual();
        assertThat(returnCapture.value()).isEqualTo(3.14d);
    }

    @Test
    void topLevelCodeCannotReturn() {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(this.getBasicReturn(ControlTokenType.RETURN, FunctionType.NONE)::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TOP_LEVEL_RETURN);
    }

    @Test
    void topLevelCodeCannotYield() {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(this.getBasicReturn(ControlTokenType.YIELD, FunctionType.NONE)::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TOP_LEVEL_RETURN);
    }

    @Test
    void inlineFunctionCanReturnWithExpression() {
        Return returnCapture = assertThatExceptionOfType(Return.class).isThrownBy(this.getBasicReturn(ControlTokenType.RETURN, FunctionType.INLINE_FUNCTION)::interpret).actual();
        assertThat(returnCapture.value()).isEqualTo(0d);
    }

    @Test
    void inlineFunctionCannotYield() {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(this.getBasicReturn(ControlTokenType.YIELD, FunctionType.INLINE_FUNCTION)::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.FUNCTION_YIELD);
    }

    @Test
    void classFunctionCanReturnWithExpression() {
        Return returnCapture = assertThatExceptionOfType(Return.class).isThrownBy(this.getBasicReturn(ControlTokenType.RETURN, FunctionType.CLASS_FUNCTION)::interpret).actual();
        assertThat(returnCapture.value()).isEqualTo(0d);
    }

    @Test
    void classFunctionCannotYield() {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(this.getBasicReturn(ControlTokenType.YIELD, FunctionType.CLASS_FUNCTION)::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.FUNCTION_YIELD);
    }

    @Test
    void functionCannotReturn() {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(this.getBasicReturn(ControlTokenType.RETURN, FunctionType.TEST)::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TEST_BLOCK_RETURN);
    }

    @Test
    void functionCanYieldWithExpression() {
        Yield yieldCapture = assertThatExceptionOfType(Yield.class).isThrownBy(this.getBasicReturn(ControlTokenType.YIELD, FunctionType.TEST)::interpret).actual();
        assertThat(yieldCapture.value()).isEqualTo(0d);
    }

    @Test
    void fieldMemberCannotReturn() {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(this.getBasicReturn(ControlTokenType.YIELD, FunctionType.FIELD_MEMBER)::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.FIELD_MEMBER_YIELD);
    }

    @Test
    void fieldMemberCannotReturnWithExpression() {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(this.getBasicReturn(ControlTokenType.YIELD, FunctionType.INITIALIZER)::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.INITIALIZER_RETURN);
    }

    @Test
    void initializerCanReturnWithExpression() {
        Return returnCapture = assertThatExceptionOfType(Return.class).isThrownBy(this.getBasicReturn(ControlTokenType.RETURN, FunctionType.FIELD_MEMBER)::interpret).actual();
        assertThat(returnCapture.value()).isEqualTo(0d);
    }

    @Test
    void initializerCannotYieldWithExpression() {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(this.getBasicReturn(ControlTokenType.YIELD, FunctionType.INITIALIZER)::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.INITIALIZER_RETURN);
    }

    @Test
    void initializerCanReturnWithoutExpression() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, "return;")
            .statementParser(new ReturnStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context ->
                context.resolver().currentFunction(FunctionType.INITIALIZER)))
            .build();
        Return returnCapture = assertThatExceptionOfType(Return.class).isThrownBy(helper::interpret).actual();
        assertThat(returnCapture.value()).isNull();
    }

    @Test
    void initializerCanYieldWithoutExpression() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, "yield;")
            .statementParser(new ReturnStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context ->
                context.resolver().currentFunction(FunctionType.INITIALIZER)))
            .build();
        Yield yieldCapture = assertThatExceptionOfType(Yield.class).isThrownBy(helper::interpret).actual();
        assertThat(yieldCapture.value()).isNull();
    }

    private HSLTestHelper getBasicReturn(ControlTokenType returnType, FunctionType functionType) {
        return HSLTestHelper.of(this.applicationContext,
                "%s 0".formatted(returnType.representation()))
            .statementParser(new ReturnStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context ->
                context.resolver().currentFunction(functionType)))
            .build();
    }
}