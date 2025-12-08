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
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ReturnStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.TestStatementParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

@HartshornIntegrationTest(includeBasePackages = false)
public class TestStatementInterpreterTests {

    @Test
    void testStatementWithYieldReturnsValue(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                        test("explicit yield") {
                            yield true;
                        }
                        """)
                .statementParser(new TestStatementParser())
                .statementParser(new BlockStatementParser())
                .statementParser(new ReturnStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .build();

        helper.interpret();

        Option<?> explicitYield = helper.context().result("explicit yield");
        Assertions.assertTrue(explicitYield.present());
        Assertions.assertEquals(true, explicitYield.get());
    }

    @Test
    void testStatementWithIncorrectReturnFailsParser(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                        test("wrong return") {
                            return true;
                        }
                        """)
                .statementParser(new TestStatementParser())
                .statementParser(new BlockStatementParser())
                .statementParser(new ReturnStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .build();

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::parse
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TEST_BODY_MUST_END_WITH_YIELD);
    }

    @Test
    void testStatementWithoutYieldFailsParser(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                        test("implicit yield") {
                            true;
                        }
                        """)
                .statementParser(new TestStatementParser())
                .statementParser(new BlockStatementParser())
                .statementParser(new ReturnStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .build();

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::parse
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.TEST_BODY_MUST_END_WITH_YIELD);
    }

    @Test
    void testStatementStatementsFailsParser(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                        test("empty body") { }
                        """)
                .statementParser(new TestStatementParser())
                .statementParser(new BlockStatementParser())
                .expressionParser(new LiteralExpressionParser())
                .build();

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::parse
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.EMPTY_TEST_BODY);
    }
}