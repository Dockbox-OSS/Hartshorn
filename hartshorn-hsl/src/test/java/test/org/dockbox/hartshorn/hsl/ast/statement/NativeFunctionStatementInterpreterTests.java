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

import org.dockbox.hartshorn.hsl.modules.AmbiguousNativeLibraryFunction;
import org.dockbox.hartshorn.hsl.modules.NativeLibrary;
import org.dockbox.hartshorn.hsl.modules.UtilityClassNativeModule;
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.NativeFunctionStatementParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import java.util.Set;

@HartshornIntegrationTest(includeBasePackages = false)
public class NativeFunctionStatementInterpreterTests {

    @Test
    void nativeFunctionImportsIndividualMethodFromModule(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, "native function math:log(a)")
            .statementParser(new NativeFunctionStatementParser())
            .statementParser(new BlockStatementParser())
            .expressionParser(new CallExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .module("math", new UtilityClassNativeModule(Math.class, applicationContext))
            .build();

        helper.interpret();

        Object logFunction = helper.findVariable("log");
        NativeLibrary nativeLogFunction =
            Assertions.assertInstanceOf(NativeLibrary.class, logFunction);
        MethodView<?, ?> originalMethod = nativeLogFunction.declaration().method();
        Assertions.assertTrue(originalMethod.declaredBy().is(Math.class));
        Assertions.assertEquals("log", originalMethod.name());
        Assertions.assertTrue(originalMethod.parameters().matches(double.class));

        // Should not import other functions from the module
        Assertions.assertNull(helper.findVariable("max"));
    }

    @Test
    void nativeFunctionImportsAmbiguousOverloadMethodsFromModule(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper =
            HSLTestHelper.of(applicationContext, "native function math:max(a, b)")
                .statementParser(new NativeFunctionStatementParser())
                .statementParser(new BlockStatementParser())
                .expressionParser(new CallExpressionParser())
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .module("math", new UtilityClassNativeModule(Math.class, applicationContext))
                .build();

        helper.interpret();

        Object maxFunction = helper.findVariable("max");
        AmbiguousNativeLibraryFunction ambiguousMaxFunction = Assertions.assertInstanceOf(
            AmbiguousNativeLibraryFunction.class,
            maxFunction
        );
        Set<NativeLibrary> overloadFunctions = ambiguousMaxFunction.libraries();
        // Math.max has 4 overloads: (int, int), (long, long), (float, float), (double, double)
        Assertions.assertEquals(4, overloadFunctions.size());
        for (NativeLibrary overloadFunction : overloadFunctions) {
            MethodView<?, ?> overloadMethod = overloadFunction.declaration().method();
            Assertions.assertTrue(overloadMethod.declaredBy().is(Math.class));
            Assertions.assertEquals("max", overloadMethod.name());
        }
    }
}