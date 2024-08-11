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
import org.dockbox.hartshorn.hsl.modules.InstanceNativeModule;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.annotations.Inject;

@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
public class InterpreterTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testAmbiguousExternalFunctionsAreAllowedByDefault() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, "ambiguousCall()");
        script.runtime().module("ambiguous", new InstanceNativeModule(this.applicationContext, new AmbiguousExternalModule()));
        Assertions.assertDoesNotThrow(script::evaluate);
    }

    @Test
    void testAmbiguousExternalFunctionsAreAllowedWhenEnabled() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, "ambiguousCall()");
        script.runtime().module("ambiguous", new InstanceNativeModule(this.applicationContext, new AmbiguousExternalModule()));
        script.runtime().interpreterOptions().permitAmbiguousExternalFunctions(true);
        Assertions.assertDoesNotThrow(script::evaluate);
    }

    @Test
    void testAmbiguousExternalFunctionsAreNotAllowedWhenDisabled() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, "ambiguousCall()");
        script.runtime().module("ambiguous", new InstanceNativeModule(this.applicationContext, new AmbiguousExternalModule()));
        script.runtime().interpreterOptions().permitAmbiguousExternalFunctions(false);
        Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
    }

    public static class AmbiguousExternalModule {

        public boolean ambiguousCall() {
            return true;
        }

        public boolean ambiguousCall(boolean value) {
            return value;
        }
    }
}
