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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.modules.AmbiguousNativeLibraryFunction;
import org.dockbox.hartshorn.hsl.modules.NativeLibrary;
import org.dockbox.hartshorn.hsl.modules.UtilityClassNativeModule;
import org.dockbox.hartshorn.hsl.parser.statement.ModuleStatementParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class ModuleStatementInterpreterTests {

    @Test
    void moduleStatementImportsKnownModuleFunctions(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, "import math")
            .statementParser(new ModuleStatementParser())
            .module("math", new UtilityClassNativeModule(Math.class, applicationContext))
            .build();
        helper.interpret();

        // Non-ambiguous function
        Object cosFunction = helper.findVariable("cos");
        NativeLibrary nativeCosFunction = assertThat(cosFunction)
                .asInstanceOf(InstanceOfAssertFactories.type(NativeLibrary.class))
                .actual();
        MethodView<?, ?> originalMethod = nativeCosFunction.declaration().method();
        assertThat(originalMethod.declaredBy().is(Math.class)).isTrue();
        assertThat(originalMethod.name()).isEqualTo("cos");
        assertThat(originalMethod.parameters().matches(double.class)).isTrue();

        // Ambiguous function (overloaded)
        Object maxFunction = helper.findVariable("max");
        AmbiguousNativeLibraryFunction ambiguousMaxFunction = assertThat(maxFunction)
                .asInstanceOf(InstanceOfAssertFactories.type(AmbiguousNativeLibraryFunction.class))
                .actual();
        Set<NativeLibrary> overloadFunctions = ambiguousMaxFunction.libraries();
        // Math.max has 4 overloads: (int, int), (long, long), (float, float), (double, double)
        assertThat(overloadFunctions).hasSize(4);
        for (NativeLibrary overloadFunction : overloadFunctions) {
            MethodView<?, ?> overloadMethod = overloadFunction.declaration().method();
            assertThat(overloadMethod.declaredBy().is(Math.class)).isTrue();
            assertThat(overloadMethod.name()).isEqualTo("max");
        }
    }

    @Test
    void moduleStatementFailsOnUnknownModule(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.of(applicationContext, "import math")
            .statementParser(new ModuleStatementParser())
            .build();

        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(helper::interpret).actual();
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.MISSING_MODULE);
    }
}