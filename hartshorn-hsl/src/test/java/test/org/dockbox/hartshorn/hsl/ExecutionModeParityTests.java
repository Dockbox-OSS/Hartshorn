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

import org.dockbox.hartshorn.hsl.CompiledScript;
import org.dockbox.hartshorn.hsl.InterpretedScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests parity between interpreted and compiled HSL execution.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
public class ExecutionModeParityTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testComputeLoopParity() {
        String scriptCode = """
                var sum = 0;
                for (var i = 1; i <= 2000; i = i + 1) {
                    sum = sum + i * 2 - (i % 3);
                }
                """;

        ScriptContext interpreted = InterpretedScript
                .of(this.applicationContext, scriptCode)
                .evaluate();

        ScriptContext compiled = CompiledScript
                .of(this.applicationContext, scriptCode)
                .compile()
                .execute();

        assertThat(compiled.result("sum").get())
                .isEqualTo(interpreted.result("sum").get());
    }

    @Test
    void testNestedLoopParity() {
        String scriptCode = """
                var count = 0;
                for (var i = 0; i < 100; i = i + 1) {
                    for (var j = 0; j < 100; j = j + 1) {
                        count = count + (i + j);
                    }
                }
                """;

        ScriptContext interpreted = InterpretedScript
                .of(this.applicationContext, scriptCode)
                .evaluate();

        ScriptContext compiled = CompiledScript
                .of(this.applicationContext, scriptCode)
                .compile()
                .execute();

        assertThat(interpreted.result("count").get())
                .isEqualTo(990000.0d);

        assertThat(compiled.result("count").get())
                .isEqualTo(990000.0d);
    }

    @Test
    void testExpressionParity() {
        String scriptCode =
                "var res = (42 * 1337) / 7 + 1024 - 256;";

        ScriptContext interpreted = InterpretedScript
                .of(this.applicationContext, scriptCode)
                .evaluate();

        ScriptContext compiled = CompiledScript
                .of(this.applicationContext, scriptCode)
                .compile()
                .execute();

        assertThat(compiled.result("res").get())
                .isEqualTo(interpreted.result("res").get());
    }
}
