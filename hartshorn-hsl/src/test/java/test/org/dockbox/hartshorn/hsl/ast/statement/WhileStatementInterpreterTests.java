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

import org.dockbox.hartshorn.hsl.parser.expression.AssignExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryComparisonExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.WhileStatementParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import java.util.concurrent.TimeUnit;

@HartshornIntegrationTest(includeBasePackages = false)
public class WhileStatementInterpreterTests {

    @Test
    void whileWithFalseExpressionNeverExecutes(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                while (false) {
                    checkpoint("inside-while")
                }
                """)
            .statementParser(new WhileStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        Assertions.assertFalse(helper.checkpoints().checkpointAccessed("inside-while"));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void whileWithConditionalExpressionExecutesUntilFalse(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                while (counter < 3) {
                    counter = checkpoint("inside-while")
                }
                """)
            .statementParser(new WhileStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new AssignExpressionParser())
            .expressionParser(new BinaryComparisonExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("counter", 0)
            .build();

        helper.interpret();
        Assertions.assertEquals(3, helper.checkpoints().checkpointAccessCount("inside-while"));
    }
}