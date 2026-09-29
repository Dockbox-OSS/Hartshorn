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
import org.dockbox.hartshorn.util.Timer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performance comparison between interpreted and compiled HSL execution.
 *
 * <p>These tests are intended to detect large performance differences and
 * regressions. They are not a substitute for statistically rigorous JMH
 * benchmarks.
 *
 * @author Guus Lieben
 *
 * @since 0.7.0
 */
@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
@Disabled("Only for manual testing")
public class ExecutionModeBenchmarkTests {

    private static final int WARMUP_ITERATIONS = 100;
    private static final int BENCHMARK_ITERATIONS = 1_000;

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testComputeLoopBenchmark() {
        String scriptCode = """
                var sum = 0;
                for (var i = 1; i <= 200; i = i + 1) {
                    sum = sum + i * 2 - (i % 3);
                }
                """;

        InterpretedScript interpreted = InterpretedScript.of(this.applicationContext, scriptCode);
        CompiledScript compiled = CompiledScript.of(this.applicationContext, scriptCode).compile();

        compiled.execute();

        benchmark(
                "Compute loop",
                interpreted::evaluate,
                compiled::execute
        );
    }

    @Test
    void testNestedLoopBenchmark() {
        String scriptCode = """
                var count = 0;
                for (var i = 0; i < 20; i = i + 1) {
                    for (var j = 0; j < 20; j = j + 1) {
                        count = count + (i + j);
                    }
                }
                """;

        InterpretedScript interpreted = InterpretedScript.of(this.applicationContext, scriptCode);
        CompiledScript compiled = CompiledScript.of(this.applicationContext, scriptCode).compile();

        BenchmarkResult result = benchmark(
                "Nested loop",
                interpreted::evaluate,
                compiled::execute
        );

        assertThat(result.interpretedResult().result("count").get())
                .isEqualTo(7600.0d);

        assertThat(result.compiledResult().result("count").get())
                .isEqualTo(7600.0d);
    }

    @Test
    void testExpressionBenchmark() {
        String scriptCode = "var res = (42 * 1337) / 7 + 1024 - 256;";

        InterpretedScript interpreted = InterpretedScript.of(this.applicationContext, scriptCode);
        CompiledScript compiled = CompiledScript.of(this.applicationContext, scriptCode).compile();

        benchmark(
                "Expression",
                interpreted::evaluate,
                compiled::execute
        );
    }

    private BenchmarkResult benchmark(
            String name,
            Supplier<ScriptContext> interpreted,
            Supplier<ScriptContext> compiled
    ) {
        // Warm up both execution modes.
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            interpreted.get();
            compiled.get();
        }

        ScriptContext interpretedResult = interpreted.get();
        ScriptContext compiledResult = compiled.get();

        long interpretedDuration = measure(interpreted);
        long compiledDuration = measure(compiled);

        double interpretedPerOperation =
                (double) interpretedDuration / BENCHMARK_ITERATIONS;

        double compiledPerOperation =
                (double) compiledDuration / BENCHMARK_ITERATIONS;

        double speedup =
                interpretedPerOperation / compiledPerOperation;

        System.out.printf(
                """
                
                %s
                  Interpreted: %.2f µs/op
                  Compiled:    %.2f µs/op
                  Speedup:     %.2fx
                %n""",
                name,
                interpretedPerOperation / 1_000,
                compiledPerOperation / 1_000,
                speedup
        );

        return new BenchmarkResult(
                interpretedResult,
                compiledResult,
                Duration.ofNanos(interpretedDuration),
                Duration.ofNanos(compiledDuration)
        );
    }

    private long measure(Supplier<ScriptContext> operation) {
        return Timer.execute(() -> {
            for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
                operation.get();
            }
        }).toNanos();
    }

    private record BenchmarkResult(
            ScriptContext interpretedResult,
            ScriptContext compiledResult,
            Duration interpretedDuration,
            Duration compiledDuration
    ) {
    }
}
