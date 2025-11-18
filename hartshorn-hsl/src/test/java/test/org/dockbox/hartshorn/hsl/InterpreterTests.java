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
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.annotations.Inject;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
class InterpreterTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void ambiguousExternalFunctionsAreAllowedByDefault() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, "ambiguousCall()");
        script.runtime().module("ambiguous", new InstanceNativeModule(this.applicationContext, new AmbiguousExternalModule()));
        assertThatCode(script::evaluate).doesNotThrowAnyException();
    }

    @Test
    void ambiguousExternalFunctionsAreAllowedWhenEnabled() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, "ambiguousCall()");
        script.runtime().module("ambiguous", new InstanceNativeModule(this.applicationContext, new AmbiguousExternalModule()));
        script.runtime().interpreterOptions().permitAmbiguousExternalFunctions(true);
        assertThatCode(script::evaluate).doesNotThrowAnyException();
    }

    @Test
    void ambiguousExternalFunctionsAreNotAllowedWhenDisabled() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, "ambiguousCall()");
        script.runtime().module("ambiguous", new InstanceNativeModule(this.applicationContext, new AmbiguousExternalModule()));
        script.runtime().interpreterOptions().permitAmbiguousExternalFunctions(false);
        assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(script::evaluate);
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
