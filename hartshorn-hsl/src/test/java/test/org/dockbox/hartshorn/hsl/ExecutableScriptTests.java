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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseExpressionValidation
public class ExecutableScriptTests {

    @Inject
    private ApplicationContext context;

    @Test
    void testHslScriptCanEvaluate() {
        String expression = "var a = 1";
        ExecutableScript script = ExecutableScript.of(this.context, expression);
        ScriptContext scriptContext = Assertions.assertDoesNotThrow(script::evaluate);
        Object result = scriptContext.interpreter().global().values().get("a");
        Assertions.assertNotNull(result);
    }

    @Test
    void testHslScriptCanResolveWithoutEvaluate() {
        String expression = "var a = 1";
        ExecutableScript script = ExecutableScript.of(this.context, expression);
        ScriptContext scriptContext = Assertions.assertDoesNotThrow(script::resolve);
        Object result = scriptContext.interpreter().global().values().get("a");
        Assertions.assertNull(result);
    }
}
