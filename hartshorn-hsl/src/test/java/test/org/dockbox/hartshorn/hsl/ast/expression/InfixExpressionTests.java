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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.HSLTestUtilities;

public class InfixExpressionTests {

    @Test
    void infixExpressionWithDefinitionAndImplementationPasses() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("""
                        "pizza" or "pasta"
                        """)
                .expressionParser(new InfixExpressionParser())
                .expressionParser(new LiteralExpressionParser());

        // Infix function definition
        FunctionParserContext functionParserContext = new FunctionParserContext();
        functionParserContext.addInfixFunction("or");
        helper.tokenParser().addContext(functionParserContext);

        // Infix function implementation
        helper.defineVariable("or", (CallableNode) (at, interpreter, instance, arguments) -> {
            Object left = arguments.getFirst();
            Object right = arguments.getLast();
            return InterpreterUtilities.isTruthy(left) ? left : right;
        });

        Object value = helper.interpretValue();
        String result = Assertions.assertInstanceOf(String.class, value);
        Assertions.assertEquals("pizza", result);
    }

    @Test
    void infixExpressionWithoutDefinitionFailsAtParser() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("""
                        "pizza" or "pasta"
                        """)
                .expressionParser(new InfixExpressionParser())
                .expressionParser(new LiteralExpressionParser());

        FunctionParserContext functionParserContext = new FunctionParserContext();
        helper.tokenParser().addContext(functionParserContext);

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::parseExpression
        );
        Assertions.assertEquals(Phase.PARSING, error.phase());
        HSLTestUtilities.assertEvaluationError(error, DiagnosticMessage.EXPECTED_EXPRESSION);
    }

    @Test
    void infixExpressionWithDefinitionAndWithoutImplementationFailsAtInterpreter() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("""
                        "pizza" or "pasta"
                        """)
                .expressionParser(new InfixExpressionParser())
                .expressionParser(new LiteralExpressionParser());

        FunctionParserContext functionParserContext = new FunctionParserContext();
        functionParserContext.addInfixFunction("or");
        helper.tokenParser().addContext(functionParserContext);

        Assertions.assertDoesNotThrow(helper::parse);

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::interpretValue
        );
        Assertions.assertEquals(Phase.INTERPRETING, error.phase());
        HSLTestUtilities.assertEvaluationError(
                error,
                FormattedDiagnostic.of(DiagnosticMessage.UNDEFINED_VARIABLE, "or")
        );
    }

    @Test
    void infixExpressionWithoutLeftArgumentFailsAtParser() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("or true")
                .expressionParser(new InfixExpressionParser())
                .expressionParser(new LiteralExpressionParser());

        FunctionParserContext functionParserContext = new FunctionParserContext();
        functionParserContext.addInfixFunction("or");
        helper.tokenParser().addContext(functionParserContext);

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::parseExpression
        );
        Assertions.assertEquals(Phase.PARSING, error.phase());
        HSLTestUtilities.assertEvaluationError(error, DiagnosticMessage.EXPECTED_EXPRESSION);
    }

    @Test
    void infixExpressionWithoutRightArgumentFailsAtParser() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("true or")
                .expressionParser(new InfixExpressionParser())
                .expressionParser(new LiteralExpressionParser());

        FunctionParserContext functionParserContext = new FunctionParserContext();
        functionParserContext.addInfixFunction("or");
        helper.tokenParser().addContext(functionParserContext);

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::parseExpression
        );
        Assertions.assertEquals(Phase.PARSING, error.phase());
        HSLTestUtilities.assertEvaluationError(error, DiagnosticMessage.EXPECTED_EXPRESSION);
    }
}