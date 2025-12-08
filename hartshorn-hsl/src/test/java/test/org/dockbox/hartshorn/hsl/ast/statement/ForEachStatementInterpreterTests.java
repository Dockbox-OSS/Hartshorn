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
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ForStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.VariableDeclarationParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.hsl.support.CheckpointModule;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

import java.util.List;
import java.util.stream.Stream;

@HartshornIntegrationTest(includeBasePackages = false)
public class ForEachStatementInterpreterTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> arrayInputs() {
        return Stream.of(
            Arguments.of(new Array(new Object[] {"a", "b", "c"})),
            Arguments.of(List.of("a", "b", "c")),
            // Note: first array is captured as varargs. Inner array
            // is the actual input array.
            Arguments.of(new Object[] {new Object[] {"a", "b", "c"}})
        );
    }

    @ParameterizedTest
    @MethodSource("arrayInputs")
    void forEachWithIterableCollectionIteratesAllEntries(Object iterable) {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                for (var item in items) {
                    checkpoint(item)
                }
                """)
            .statementParser(new ForStatementParser())
            .statementParser(new BlockStatementParser())
            .statementParser(new VariableDeclarationParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineGlobal("items", iterable)
            .build();

        helper.interpret();

        CheckpointModule checkpoints = helper.checkpoints();
        Assertions.assertEquals(3, checkpoints.checkpoints().size());
        Assertions.assertTrue(checkpoints.checkpointAccessed("a"));
        Assertions.assertTrue(checkpoints.checkpointAccessed("b"));
        Assertions.assertTrue(checkpoints.checkpointAccessed("c"));
    }

    @Test
    void forEachWithNonIterableCollectionFails() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                for (var item in items) {
                    checkpoint(item)
                }
                """)
            .statementParser(new ForStatementParser())
            .statementParser(new BlockStatementParser())
            .statementParser(new VariableDeclarationParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineGlobal("items", "not an iterable")
            .build();

        ScriptEvaluationError error = Assertions.assertThrows(
            ScriptEvaluationError.class,
            helper::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.NON_ITERABLE_COLLECTION);
    }

    @Test
    void forEachWithEmptyCollectionDoesNotIterate() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                for (var item in items) {
                    checkpoint(item)
                }
                """)
            .statementParser(new ForStatementParser())
            .statementParser(new BlockStatementParser())
            .statementParser(new VariableDeclarationParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineGlobal("items", List.of())
            .build();

        helper.interpret();

        CheckpointModule checkpoints = helper.checkpoints();
        Assertions.assertEquals(0, checkpoints.checkpoints().size());
    }
}