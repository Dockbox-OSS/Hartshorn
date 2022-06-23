package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.callable.InstanceNativeModule;
import org.dockbox.hartshorn.hsl.runtime.StandardLibraryHslRuntime;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import jakarta.inject.Inject;

@HartshornTest
public class PredefinedScriptTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> scripts() {
        final Path resources = Paths.get("src", "test", "resources");
        return Arrays.stream(resources.toFile().listFiles())
                .filter(file -> file.getName().endsWith(".hsl"))
                .map(file -> {
                    try {
                        return Files.readAllLines(file.toPath());
                    }
                    catch (final IOException e) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .map(lines -> String.join("\n", lines))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testPredefinedScript(final String content) {
        final StandardLibraryHslRuntime runtime = new StandardLibraryHslRuntime(this.applicationContext);
        Assertions.assertDoesNotThrow(() -> runtime.run(content));
        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
    }

    @Test
    void testExpression() {
        final ValidateExpressionRuntime runtime = new ValidateExpressionRuntime(this.applicationContext);
        final String expression = "1 == 1";
        runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
        Assertions.assertTrue(runtime.valid());
    }

    @Test
    void testComplexExpression() {
        final ValidateExpressionRuntime runtime = new ValidateExpressionRuntime(this.applicationContext);
        final String expression = "1 + 3 == 4 and 2 + 2 == 4 and 2 - 2 == 0";
        runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
        Assertions.assertTrue(runtime.valid());
    }

    @Test
    void testExpressionWithGlobal() {
        final ValidateExpressionRuntime runtime = new ValidateExpressionRuntime(this.applicationContext);
        runtime.global("a", 12);
        final String expression = "a == 12";
        runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
        Assertions.assertTrue(runtime.valid());
    }

    @Test
    void testExpressionWithGlobalFunctionAccess() {
        final ValidateExpressionRuntime runtime = new ValidateExpressionRuntime(this.applicationContext);
        runtime.global("context", this.applicationContext);
        final String expression = "context != nil and context.log() != nil";
        runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
        Assertions.assertTrue(runtime.valid());
    }

    @Test
    void testScriptWithGlobalFunctionAccess() {
        final StandardLibraryHslRuntime runtime = new StandardLibraryHslRuntime(this.applicationContext);
        runtime.global("context", this.applicationContext);
        final String expression = "context.log().info(\"Hello world!\");";
        runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
    }

    @Test
    void testExpressionWithNativeAccess() {
        final ValidateExpressionRuntime runtime = new ValidateExpressionRuntime(this.applicationContext);
        final String expression = "log() != null";
        runtime.module("application", new InstanceNativeModule(this.applicationContext));
        runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
        Assertions.assertTrue(runtime.valid());
    }
}
