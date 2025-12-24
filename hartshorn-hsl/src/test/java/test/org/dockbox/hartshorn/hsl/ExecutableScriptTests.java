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
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
class ExecutableScriptTests {

    @Inject
    private ApplicationContext context;

    @Test
    void hslScriptCanEvaluate() {
        String expression = "var a = 1";
        ExecutableScript script = ExecutableScript.of(this.context, expression);
        ScriptContext scriptContext = script.evaluate();
        Object result = scriptContext.interpreter().global().values().get("a");
        assertThat(result).isNotNull();
    }

    @Test
    void hslScriptCanResolveWithoutEvaluate() {
        String expression = "var a = 1";
        ExecutableScript script = ExecutableScript.of(this.context, expression);
        ScriptContext scriptContext = script.resolve();
        Object result = scriptContext.interpreter().global().values().get("a");
        assertThat(result).isNull();
    }
}
