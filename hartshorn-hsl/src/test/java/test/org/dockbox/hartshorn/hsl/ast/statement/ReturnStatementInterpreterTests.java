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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

@HartshornIntegrationTest(includeBasePackages = false)
public class ReturnStatementInterpreterTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void returnStatementThrowsCaptureExceptionForReturn() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, "return 42")
                .statementParser(new ReturnStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context -> {
                    // Cannot return from top-level code, so we mark the current context
                    // as being inside a function
                    context.resolver().currentFunction(FunctionType.INLINE_FUNCTION);
                }))
                .build();

        Return returnCapture = Assertions.assertThrows(Return.class, helper::interpret);
        Assertions.assertEquals(42d, returnCapture.value());
    }

    @Test
    void returnStatementThrowsCaptureExceptionForYield() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, "return 3.14")
                .statementParser(new ReturnStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context -> {
                    // Cannot return from top-level code, so we mark the current context
                    // as being inside a generator function
                    context.resolver().currentFunction(FunctionType.FIELD_MEMBER);
                }))
                .build();

        Return returnCapture = Assertions.assertThrows(Return.class, helper::interpret);
        Assertions.assertEquals(3.14d, returnCapture.value());
    }

    @Test
    void topLevelCodeCannotReturn() {
        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
            this.getBasicReturn(ControlTokenType.RETURN, FunctionType.NONE)::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TOP_LEVEL_RETURN);
    }
    @Test
    void topLevelCodeCannotYield() {
        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
            this.getBasicReturn(ControlTokenType.YIELD, FunctionType.NONE)::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TOP_LEVEL_RETURN);
    }

    @Test
    void inlineFunctionCanReturnWithExpression() {
        Return returnCapture = Assertions.assertThrows(
                Return.class,
            this.getBasicReturn(ControlTokenType.RETURN, FunctionType.INLINE_FUNCTION)::interpret
        );
        Assertions.assertEquals(0d, returnCapture.value());
    }

    @Test
    void inlineFunctionCannotYield() {
        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
            this.getBasicReturn(ControlTokenType.YIELD, FunctionType.INLINE_FUNCTION)::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.FUNCTION_YIELD);
    }

    @Test
    void classFunctionCanReturnWithExpression() {
        Return returnCapture = Assertions.assertThrows(
                Return.class,
            this.getBasicReturn(ControlTokenType.RETURN, FunctionType.CLASS_FUNCTION)::interpret
        );
        Assertions.assertEquals(0d, returnCapture.value());
    }

    @Test
    void classFunctionCannotYield() {
        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
            this.getBasicReturn(ControlTokenType.YIELD, FunctionType.CLASS_FUNCTION)::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.FUNCTION_YIELD);
    }

    @Test
    void testFunctionCannotReturn() {
        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
            this.getBasicReturn(ControlTokenType.RETURN, FunctionType.TEST)::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TEST_BLOCK_RETURN);
    }

    @Test
    void testFunctionCanYieldWithExpression() {
        Yield yieldCapture = Assertions.assertThrows(
                Yield.class,
            this.getBasicReturn(ControlTokenType.YIELD, FunctionType.TEST)::interpret
        );
        Assertions.assertEquals(0d, yieldCapture.value());
    }

    @Test
    void fieldMemberCannotReturn() {
        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
            this.getBasicReturn(ControlTokenType.YIELD, FunctionType.FIELD_MEMBER)::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.FIELD_MEMBER_YIELD);
    }

    @Test
    void fieldMemberCannotReturnWithExpression() {
        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
            this.getBasicReturn(ControlTokenType.YIELD, FunctionType.INITIALIZER)::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.INITIALIZER_RETURN);
    }

    @Test
    void initializerCanReturnWithExpression() {
        Return returnCapture = Assertions.assertThrows(
                Return.class,
            this.getBasicReturn(ControlTokenType.RETURN, FunctionType.FIELD_MEMBER)::interpret
        );
        Assertions.assertEquals(0d, returnCapture.value());
    }

    @Test
    void initializerCannotYieldWithExpression() {
        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
            this.getBasicReturn(ControlTokenType.YIELD, FunctionType.INITIALIZER)::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.INITIALIZER_RETURN);
    }

    @Test
    void initializerCanReturnWithoutExpression() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, "return;")
                .statementParser(new ReturnStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context -> {
                    context.resolver().currentFunction(FunctionType.INITIALIZER);
                }))
                .build();
        Return returnCapture = Assertions.assertThrows(
                Return.class,
                helper::interpret
        );
        Assertions.assertNull(returnCapture.value());
    }

    @Test
    void initializerCanYieldWithoutExpression() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, "yield;")
                .statementParser(new ReturnStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context -> {
                    context.resolver().currentFunction(FunctionType.INITIALIZER);
                }))
                .build();
        Yield yieldCapture = Assertions.assertThrows(
                Yield.class,
                helper::interpret
        );
        Assertions.assertNull(yieldCapture.value());
    }

    private HSLTestHelper getBasicReturn(ControlTokenType returnType, FunctionType functionType) {
        return HSLTestHelper.of(this.applicationContext, "%s 0".formatted(returnType.representation()))
                .statementParser(new ReturnStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context -> {
                    context.resolver().currentFunction(functionType);
                }))
                .build();
    }
}