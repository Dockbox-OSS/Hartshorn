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
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.HSLTestUtilities;

public class PostfixExpressionTests {

    @Test
    void postFixIncrementYieldsOriginalValueAndIncrementsVariable() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("a++")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        helper.defineVariable("a", 5);

        Object expressionResult = helper.interpretValue();
        // Note that the result is expected to be int 5, rather than double 5.0
        Assertions.assertEquals(5, expressionResult);

        Object variableValue = helper.findVariable("a");
        Assertions.assertEquals(6d, variableValue);
    }

    @Test
    void postFixIncrementOnNonNumberValueFails() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("a++")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        String actual = "not a number";
        helper.defineVariable("a", actual);

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::interpretValue
        );
        HSLTestUtilities.assertEvaluationError(
                error,
                FormattedDiagnostic.of(DiagnosticMessage.NON_NUMBER_OPERAND, actual)
        );
    }

    @Test
    void postFixDecrementYieldsOriginalValueAndDecrementsVariable() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("a--")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        helper.defineVariable("a", 5);

        Object expressionResult = helper.interpretValue();
        // Note that the result is expected to be int 5, rather than double 5.0
        Assertions.assertEquals(5, expressionResult);

        Object variableValue = helper.findVariable("a");
        Assertions.assertEquals(4d, variableValue);
    }

    @Test
    void postFixDecrementOnNonNumberValueFails() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("a--")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        String actual = "not a number";
        helper.defineVariable("a", actual);

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::interpretValue
        );
        HSLTestUtilities.assertEvaluationError(
                error,
                FormattedDiagnostic.of(DiagnosticMessage.NON_NUMBER_OPERAND, actual)
        );
    }
}