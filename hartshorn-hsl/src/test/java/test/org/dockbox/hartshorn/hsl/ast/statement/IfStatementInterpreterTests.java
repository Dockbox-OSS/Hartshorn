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

import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.IfStatementParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class IfStatementInterpreterTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void ifStatementEvaluatesIfConditionIsTrue() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                if (true) {
                    checkpoint("inside-true");
                }
                """)
            .statementParser(new IfStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-true")).isTrue();
    }

    @Test
    void ifStatementEvaluatesIfConditionIsTruthy() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                if ("truthy-value") {
                    checkpoint("inside-true");
                }
                """)
            .statementParser(new IfStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-true")).isTrue();
    }

    @Test
    void ifStatementDoesNotEvaluateIfConditionIsFalse() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                if (false) {
                    checkpoint("inside-true");
                }
                """)
            .statementParser(new IfStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-true")).isFalse();
    }

    @Test
    void ifStatementDoesNotEvaluateIfConditionIsFalsy() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                if (null) {
                    checkpoint("inside-true");
                }
                """)
            .statementParser(new IfStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-true")).isFalse();
    }

    @Test
    void ifStatementDoesnotExecuteElseBranchIfConditionTrue() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                if (true) {
                    checkpoint("inside-true");
                }
                else {
                    checkpoint("inside-false");
                }
                """)
            .statementParser(new IfStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-true")).isTrue();
        assertThat(helper.checkpoints().checkpointAccessed("inside-false")).isFalse();
    }

    @Test
    void ifStatementDoesNotExecuteElseBranchIfConditionTruthy() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                if ("truthy-value") {
                    checkpoint("inside-true");
                }
                else {
                    checkpoint("inside-false");
                }
                """)
            .statementParser(new IfStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-true")).isTrue();
        assertThat(helper.checkpoints().checkpointAccessed("inside-false")).isFalse();
    }

    @Test
    void ifStatementExecutesElseBranchIfConditionFalse() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                if (false) {
                    checkpoint("inside-true");
                }
                else {
                    checkpoint("inside-false");
                }
                """)
            .statementParser(new IfStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-true")).isFalse();
        assertThat(helper.checkpoints().checkpointAccessed("inside-false")).isTrue();
    }

    @Test
    void ifStatementExecutesElseBranchIfConditionFalsy() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                if (null) {
                    checkpoint("inside-true");
                }
                else {
                    checkpoint("inside-false");
                }
                """)
            .statementParser(new IfStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        helper.interpret();
        assertThat(helper.checkpoints().checkpointAccessed("inside-true")).isFalse();
        assertThat(helper.checkpoints().checkpointAccessed("inside-false")).isTrue();
    }
}
