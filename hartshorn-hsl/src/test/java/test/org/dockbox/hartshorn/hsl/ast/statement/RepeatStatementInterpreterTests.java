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
import org.dockbox.hartshorn.hsl.parser.expression.AssignExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryAdditionExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.UnaryExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.RepeatStatementParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

@HartshornIntegrationTest(includeBasePackages = false)
public class RepeatStatementInterpreterTests {

    @Test
    void repeatWithNonZeroValueRepeatsExactlyNTimes(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                        repeat (3) {
                            counter = counter + 1
                        }
                        """)
                .statementParser(new RepeatStatementParser())
                .statementParser(new BlockStatementParser())
                .expressionParser(new AssignExpressionParser())
                .expressionParser(new BinaryAdditionExpressionParser())
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .defineLocal("counter", 0)
                .build();

        helper.interpret();

        Object counter = helper.findVariable("counter");
        Assertions.assertEquals(3d, counter);
    }

    @Test
    void repeatWithNegativeValueFails(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, "repeat (-1) { }")
                .statementParser(new RepeatStatementParser())
                .statementParser(new BlockStatementParser())
                .expressionParser(new UnaryExpressionParser())
                .expressionParser(new LiteralExpressionParser())
                .build();

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::interpret
        );
        ScriptAssertions.assertEvaluationError(
                error,
                FormattedDiagnostic.of(DiagnosticMessage.ILLEGAL_NEGATIVE_NUMBER, -1d)
        );
    }

    @Test
    void repeatWithNonNumberValueFails(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, "repeat (null) { }")
                .statementParser(new RepeatStatementParser())
                .statementParser(new BlockStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .build();

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::interpret
        );
        ScriptAssertions.assertEvaluationError(
                error,
                FormattedDiagnostic.of(DiagnosticMessage.NON_NUMBER_OPERAND, (Object) null)
        );
    }
}