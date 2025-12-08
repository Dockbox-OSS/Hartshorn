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

package test.org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
public class FinalizedTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void cannotExtendFinalClass() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final class User { }
                class Admin extends User { }
                """);
        ScriptAssertions.assertEvaluationFails(script, FormattedDiagnostic.builder()
            .message(DiagnosticMessage.ILLEGAL_FINAL_SUPER_TYPE)
            .argument("User")
            .build());
    }

    @Test
    void canExtendNonFinalExternalClass() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                class Admin extends User { }
                """);
        script.runtime().imports(User.class);
        Assertions.assertDoesNotThrow(script::evaluate);
    }

    @Test
    void cannotExtendFinalExternalClass() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                class Admin extends FinalUser { }
                """);
        script.runtime().imports(FinalUser.class);
        ScriptAssertions.assertEvaluationFails(script, FormattedDiagnostic.builder()
            .message(DiagnosticMessage.ILLEGAL_FINAL_SUPER_TYPE)
            .argument("FinalUser")
            .build());
    }

    @Test
    void testCannotReassignFinalVariables() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final var x = 1;
                x = 2;
                """);
        ScriptAssertions.assertEvaluationFails(script, FormattedDiagnostic.builder()
            .message(DiagnosticMessage.ILLEGAL_FINAL_X_REASSIGNMENT)
            .argument("variable")
            .argument("x")
            .build());
    }

    @Test
    void testCannotReassignFinalFunctions() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final function x() { }
                function x() { }
                """);
        ScriptAssertions.assertEvaluationFails(script, FormattedDiagnostic.builder()
            .message(DiagnosticMessage.ILLEGAL_FINAL_X_REASSIGNMENT)
            .argument("function")
            .argument("x")
            .build());
    }

    @Test
    void testCannotReassignFinalClasses() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final class User { }
                class User { }
                """);
        ScriptAssertions.assertEvaluationFails(script, FormattedDiagnostic.builder()
            .message(DiagnosticMessage.ILLEGAL_FINAL_X_REASSIGNMENT)
            .argument("class")
            .argument("User")
            .build());
    }

    @Test
    void testCannotReassignFinalNativeFunctions() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final native function a:x();
                function x() { }
                """);
        // Do not evaluate, as the native function does not exist in the current environment.
        ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::resolve);
        ScriptAssertions.assertEvaluationError(error, FormattedDiagnostic.builder()
                        .message(DiagnosticMessage.ILLEGAL_FINAL_X_REASSIGNMENT)
                        .argument("native function")
                        .argument("x")
                        .build());
        ScriptAssertions.assertEvaluationFailedAtPosition(error, 2, 9);
    }

    public static class User {
    }

    public static final class FinalUser {
    }
}
