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
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BreakStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.CaseBodyStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.SwitchStatementParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

@HartshornIntegrationTest(includeBasePackages = false)
public class SwitchStatementInterpreterTests {

    @Test
    void defaultCaseMatchesIfNoCasesPresent(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                switch (value) {
                    default:
                        checkpoint("default-case")
                        break
                }
                """)
            .statementParser(new SwitchStatementParser(new CaseBodyStatementParser()))
            .statementParser(new BreakStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("value", "some-value")
            .build();

        helper.interpret();

        Assertions.assertTrue(helper.checkpoints().checkpointAccessed("default-case"));
    }

    @Test
    void matchingCaseExecutesOverDefault(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                switch (value) {
                    case "match":
                        checkpoint("matching-case")
                        break
                    default:
                        checkpoint("default-case")
                        break
                }
                """)
            .statementParser(new SwitchStatementParser(new CaseBodyStatementParser()))
            .statementParser(new BreakStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("value", "match")
            .build();

        helper.interpret();

        Assertions.assertTrue(helper.checkpoints().checkpointAccessed("matching-case"));
        Assertions.assertFalse(helper.checkpoints().checkpointAccessed("default-case"));
    }

    @Test
    void switchWithoutCasesFailsAtParser(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                switch (value) {
                }
                """)
            .statementParser(new SwitchStatementParser(new CaseBodyStatementParser()))
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("value", "some-value")
            .build();

        ScriptEvaluationError error = Assertions.assertThrows(
            ScriptEvaluationError.class,
            helper::interpret
        );
        ScriptAssertions.assertEvaluationError(
            error,
            DiagnosticMessage.SWITCH_MUST_HAVE_CASE_OR_DEFAULT
        );
    }
}