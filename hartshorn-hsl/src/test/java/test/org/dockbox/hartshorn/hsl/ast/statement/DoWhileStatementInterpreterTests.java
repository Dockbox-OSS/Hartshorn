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
import org.dockbox.hartshorn.hsl.parser.statement.DoWhileStatementParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class DoWhileStatementInterpreterTests {

    @Test
    void doWhileWithFalseExpressionExecutesOnce(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                do {
                    checkpoint("inside-do-while")
                } while (false)
                """)
            .statementParser(new DoWhileStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-do-while")).isTrue();
        assertThat(helper.checkpoints().checkpointAccessCount("inside-do-while")).isOne();
    }

    @Test
    void doWhileWithConditionalExpressionExecutesUntilFalse(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                do {
                    counter = checkpoint("inside-do-while")
                } while (counter < 3)
                """)
            .statementParser(new DoWhileStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new AssignExpressionParser())
            .expressionParser(new BinaryComparisonExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("counter", 0)
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessCount("inside-do-while")).isEqualTo(3);
    }
}