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

import java.util.Map;

import org.dockbox.hartshorn.hsl.CompiledScript;
import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.InterpretedScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ExecutionMode;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
class CompiledScriptTests {

    @Inject
    private ApplicationContext context;

    @Test
    void testBasicArithmeticCompilation() {
        CompiledScript script = CompiledScript.of(this.context, "var result = (10 + 20) * 3 - 5 / 2;");
        CompiledScript compiled = script.compile();
        assertThat(compiled).isNotNull();
        assertThat(compiled.bytecode()).isNotEmpty();

        ScriptContext resultContext = compiled.execute();
        assertThat(resultContext.result("result").get()).isEqualTo(87.5d);
    }

    @Test
    void testCompiledScriptDirectExecutionMode() {
        CompiledScript script = CompiledScript.of(this.context, "var x = 42; var y = x + 8;");
        ScriptContext resultContext = script.evaluate();

        assertThat(resultContext.result("x").get()).isEqualTo(42.0d);
        assertThat(resultContext.result("y").get()).isEqualTo(50.0d);
    }

    @Test
    void testWhileLoopAndControlFlow() {
        String code = """
            var sum = 0;
            var i = 1;
            while (i <= 10) {
                sum = sum + i;
                i = i + 1;
            }
            """;
        CompiledScript script = CompiledScript.of(this.context, code);
        ScriptContext resultContext = script.evaluate();

        assertThat(resultContext.result("sum").get()).isEqualTo(55.0d);
    }

    @Test
    void testDoWhileLoop() {
        String code = """
            var count = 0;
            do {
                count = count + 1;
            } while (count < 5);
            """;
        CompiledScript script = CompiledScript.of(this.context, code);
        ScriptContext resultContext = script.evaluate();

        assertThat(resultContext.result("count").get()).isEqualTo(5.0d);
    }

    @Test
    void testForLoopWithBreakAndContinue() {
        String code = """
            var total = 0;
            for (var i = 0; i < 10; i = i + 1) {
                if (i == 2) {
                    continue;
                }
                if (i == 7) {
                    break;
                }
                total = total + i;
            }
            """;
        CompiledScript script = CompiledScript.of(this.context, code);
        ScriptContext resultContext = script.evaluate();

        // 0 + 1 + 3 + 4 + 5 + 6 = 19
        assertThat(resultContext.result("total").get()).isEqualTo(19.0d);
    }

    @Test
    void testRepeatLoop() {
        String code = """
            var count = 0;
            repeat (5) {
                count = count + 2;
            }
            """;
        CompiledScript script = CompiledScript.of(this.context, code);
        ScriptContext resultContext = script.evaluate();

        assertThat(resultContext.result("count").get()).isEqualTo(10.0d);
    }

    @Test
    void testSwitchStatement() {
        String code = """
            var choice = 2;
            var output = "none";
            switch (choice) {
                case 1: {
                    output = "one";
                }
                case 2: {
                    output = "two";
                }
                default: {
                    output = "default";
                }
            }
            """;
        CompiledScript script = CompiledScript.of(this.context, code);
        ScriptContext resultContext = script.evaluate();

        assertThat(resultContext.result("output").get()).isEqualTo("two");
    }

    @Test
    void testTernaryAndElvis() {
        String code = """
            var a = true ? 100 : 200;
            var b = false ? 100 : 200;
            var c = null ?: 42;
            """;
        CompiledScript script = CompiledScript.of(this.context, code);
        ScriptContext resultContext = script.evaluate();

        assertThat(resultContext.result("a").get()).isEqualTo(100.0d);
        assertThat(resultContext.result("b").get()).isEqualTo(200.0d);
        assertThat(resultContext.result("c").get()).isEqualTo(42.0d);
    }

    @Test
    void testFunctionDeclarationAndInvocation() {
        String code = """
            function add(a, b) {
                return a + b;
            }
            var sum = add(15, 27);
            """;
        CompiledScript script = CompiledScript.of(this.context, code);
        ScriptContext resultContext = script.evaluate();

        assertThat(resultContext.result("sum").get()).isEqualTo(42.0d);
    }

    @Test
    void testCompiledScriptReusabilityWithParameters() {
        String code = "var result = a * b;";
        CompiledScript compiled = CompiledScript.of(this.context, code).compile();

        ScriptContext firstRun = compiled.execute(Map.of("a", 6.0d, "b", 7.0d));
        assertThat(firstRun.result("result").get()).isEqualTo(42.0d);

        ScriptContext secondRun = compiled.execute(Map.of("a", 9.0d, "b", 8.0d));
        assertThat(secondRun.result("result").get()).isEqualTo(72.0d);
    }

    @ParameterizedTest
    @EnumSource(value = ExecutionMode.class, names = {"INTERPRETED", "COMPILED"})
    void testExecutionModeParity(ExecutionMode mode) {
        String code = """
            var x = 10;
            var y = 20;
            var z = 0;
            if (x < y) {
                z = (x + y) * 2;
            } else {
                z = -1;
            }
            """;
        ExecutableScript script = mode == ExecutionMode.COMPILED
            ? CompiledScript.of(this.context, code)
            : InterpretedScript.of(this.context, code);
        ScriptContext resultContext = script.evaluate();

        assertThat(resultContext.result("z").get()).isEqualTo(60.0d);
    }
}
