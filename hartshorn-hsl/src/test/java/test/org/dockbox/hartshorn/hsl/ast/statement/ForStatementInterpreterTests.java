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

import org.dockbox.hartshorn.hsl.parser.expression.BinaryComparisonExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ForStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.VariableDeclarationParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

@HartshornIntegrationTest(includeBasePackages = false)
public class ForStatementInterpreterTests {

    @Test
    void forLoopWithFalseConditionNeverExecutes(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                for (var i = 0; false; i++) {
                    checkpoint("inside-for-loop")
                }
                """)
            .statementParser(new ForStatementParser())
            .statementParser(new BlockStatementParser())
            .statementParser(new VariableDeclarationParser())
            .expressionParser(new CallExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .build();

        helper.interpret();
        Assertions.assertFalse(helper.checkpoints().checkpointAccessed("inside-for-loop"));
    }

    @Test
    void forLoopExecutesUntilConditionIsFalse(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                for (var i = 0; i < 3; i++) {
                    checkpoint("inside-for-loop")
                }
                """)
            .statementParser(new ForStatementParser())
            .statementParser(new BlockStatementParser())
            .statementParser(new VariableDeclarationParser())
            .expressionParser(new CallExpressionParser())
            .expressionParser(new BinaryComparisonExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .build();

        helper.interpret();
        Assertions.assertTrue(helper.checkpoints().checkpointAccessed("inside-for-loop"));
        Assertions.assertEquals(3, helper.checkpoints().checkpointAccessCount("inside-for-loop"));
    }
}