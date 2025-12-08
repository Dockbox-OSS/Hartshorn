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
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ConstructorStatementParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.semantic.ClassType;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

@HartshornIntegrationTest(includeBasePackages = false)
public class ConstructorStatementInterpreterTests {

    @Test
    void constructorDefinesInitializer(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                constructor(name, age) { }
                """)
            .statementParser(new ConstructorStatementParser())
            .statementParser(new BlockStatementParser())
            .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context -> {
                context.resolver().currentClassType(ClassType.CLASS);
            }))
            .build();

        helper.interpret();

        Object constructor = helper.findVariable(FunctionTokenType.CONSTRUCTOR.defaultLexeme());
        VirtualFunction virtualFunction = Assertions.assertInstanceOf(
            VirtualFunction.class,
            constructor
        );
        Assertions.assertTrue(virtualFunction.isInitializer());
    }

    @Test
    void constructorOutsideClassFails(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, """
                constructor(name, age) { }
                """)
            .statementParser(new ConstructorStatementParser())
            .statementParser(new BlockStatementParser())
            .build();

        ScriptEvaluationError error = Assertions.assertThrows(
            ScriptEvaluationError.class,
            helper::interpret
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.CONSTRUCTOR_OUTSIDE_CLASS);
    }
}