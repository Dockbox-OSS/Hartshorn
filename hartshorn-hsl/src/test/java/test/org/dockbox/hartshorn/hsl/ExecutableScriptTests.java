/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.hsl.CompiledExpressionScript;
import org.dockbox.hartshorn.hsl.CompiledScript;
import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.InterpretedExpressionScript;
import org.dockbox.hartshorn.hsl.InterpretedScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ExecutionMode;
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
    void interpretedScriptEvaluatesCorrectly() {
        String expression = "var a = 1;";
        InterpretedScript script = InterpretedScript.of(this.context, expression);
        assertThat(script.mode()).isEqualTo(ExecutionMode.INTERPRETED);
        ScriptContext scriptContext = script.evaluate();
        Object result = scriptContext.interpreter().global().values().get("a");
        assertThat(result).isEqualTo(1.0d);
    }

    @Test
    void compiledScriptEvaluatesCorrectly() {
        String expression = "var a = 42;";
        CompiledScript script = CompiledScript.of(this.context, expression);
        assertThat(script.mode()).isEqualTo(ExecutionMode.COMPILED);
        ScriptContext scriptContext = script.evaluate();
        assertThat(scriptContext.result("a").get()).isEqualTo(42.0d);
    }

    @Test
    void compiledScriptCanCompileDirectly() {
        String expression = "var a = 100;";
        CompiledScript script = CompiledScript.of(this.context, expression);
        CompiledScript compiled = script.compile();
        ScriptContext result = compiled.execute();
        assertThat(result.result("a").get()).isEqualTo(100.0d);
    }

    @Test
    void interpretedScriptCanResolveWithoutEvaluate() {
        String expression = "var a = 1;";
        InterpretedScript script = InterpretedScript.of(this.context, expression);
        ScriptContext scriptContext = script.resolve();
        Object result = scriptContext.interpreter().global().values().get("a");
        assertThat(result).isNull();
    }

    @Test
    void compiledScriptCanResolveWithoutEvaluate() {
        String expression = "var a = 1;";
        CompiledScript script = CompiledScript.of(this.context, expression);
        ScriptContext scriptContext = script.resolve();
        assertThat(scriptContext.statements()).isNotEmpty();
    }

    @Test
    void expressionScriptsValidateCorrectly() {
        InterpretedExpressionScript interpreted = InterpretedExpressionScript.of(this.context, "1 + 1 == 2");
        assertThat(interpreted.mode()).isEqualTo(ExecutionMode.INTERPRETED);
        assertThat(interpreted.valid()).isTrue();

        CompiledExpressionScript compiled = CompiledExpressionScript.of(this.context, "1 + 1 == 2");
        assertThat(compiled.mode()).isEqualTo(ExecutionMode.COMPILED);
        compiled.execute();
        assertThat(compiled.valid()).isTrue();
    }
}
