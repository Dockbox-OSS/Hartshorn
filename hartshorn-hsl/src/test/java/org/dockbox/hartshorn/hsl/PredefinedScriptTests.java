package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.callable.module.InstanceNativeModule;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
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
import java.util.Map;
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
        final StandardRuntime runtime = new StandardRuntime(this.applicationContext);
        Assertions.assertDoesNotThrow(() -> runtime.run(content));
        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testPredefinedScriptWithOptionalSemicolons(final String content) {
        final String source = content.replaceAll(";", "");
        final StandardRuntime runtime = new StandardRuntime(this.applicationContext);
        Assertions.assertDoesNotThrow(() -> runtime.run(source));
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
        final String expression = "context != null and context.log() != null";
        runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
        Assertions.assertTrue(runtime.valid());
    }

    @Test
    void testScriptWithGlobalFunctionAccess() {
        final StandardRuntime runtime = new StandardRuntime(this.applicationContext);
        runtime.global("context", this.applicationContext);
        final String expression = "context.log().info(\"Hello world!\")";
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

    @Test
    void testMultilineWithComments() {
        final StandardRuntime runtime = new StandardRuntime(this.applicationContext);
        final String expression = """
                print("Hello world!");
                
                # This is a comment
                print("Hello world 2!");
                
                // This is also a comment, print("Hello world 3!");
                print("Hello world 4!");
                
                /* This is a multi-line comment
                see?!
                */
                print("Hello world 5!");
                """;
        runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
        Assertions.assertEquals(3, runtime.comments().size());

        // Comments are not trimmed, so we need to include spaces in the expected result
        Assertions.assertEquals(" This is a comment", runtime.comments().get(0).text());
        Assertions.assertEquals(" This is also a comment, print(\"Hello world 3!\");", runtime.comments().get(1).text());
        Assertions.assertEquals(" This is a multi-line comment\nsee?!\n", runtime.comments().get(2).text());
    }

    @Test
    void testGlobalResultTracking() {
        final StandardRuntime runtime = new StandardRuntime(this.applicationContext);
        final String expression = """
                var a = 12;
                var b = 13;
                var c = a + b;
                """;
        final Map<String, Object> results = runtime.run(expression);

        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
        Assertions.assertFalse(results.isEmpty());

        Assertions.assertEquals(12d, results.get("a"));
        Assertions.assertEquals(13d, results.get("b"));
        Assertions.assertEquals(25d, results.get("c"));
    }
}
