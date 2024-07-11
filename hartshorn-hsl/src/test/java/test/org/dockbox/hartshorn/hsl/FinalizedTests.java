/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.annotations.Inject;

@HartshornTest(includeBasePackages = false)
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
        ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
        Assertions.assertEquals("""
                Cannot extend final class 'User'. While interpreting at line 2, column 23.
                class Admin extends User { }
                                       ^""", error.getMessage());
    }

    @Test
    void canExtendExternalNonFinalClass() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                class Admin extends User { }
                """);
        script.runtime().imports(User.class);
        Assertions.assertDoesNotThrow(script::evaluate);
    }

    @Test
    void cannotExtendFinalExternalClass() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                class Admin extends User { }
                """);
        script.runtime().imports("User", FinalUser.class);
        ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
        Assertions.assertEquals("""
                Cannot extend final class 'FinalUser'. While interpreting at line 1, column 20.
                class Admin extends User { }
                                    ^""", error.getMessage());
    }

    @Test
    void testCannotReassignFinalVariables() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final var x = 1;
                x = 2;
                """);
        ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
        Assertions.assertEquals("""
                Cannot reassign final variable 'x'. While resolving at line 2, column 0.
                x = 2;
                ^""", error.getMessage());
    }

    @Test
    void testCannotReassignFinalFunctions() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final function x() { }
                function x() { }
                """);
        ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
        Assertions.assertEquals("""
                Cannot reassign final function 'x'. While resolving at line 2, column 9.
                function x() { }
                         ^""", error.getMessage());
    }

    @Test
    void testCannotReassignFinalClasses() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final class User { }
                class User { }
                """);
        ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
        Assertions.assertEquals("""
                Cannot reassign final class 'User'. While resolving at line 2, column 9.
                class User { }
                         ^""", error.getMessage());
    }

    @Test
    void testCannotReassignFinalNativeFunctions() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                final native function a.x();
                function x() { }
                """);
        // Do not evaluate, as the native function does not exist in the current environment.
        ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::resolve);
        Assertions.assertEquals("""
                Cannot reassign final native function 'x'. While resolving at line 2, column 9.
                function x() { }
                         ^""", error.getMessage());
    }

    public static class User { }
    public static final class FinalUser { }
}
