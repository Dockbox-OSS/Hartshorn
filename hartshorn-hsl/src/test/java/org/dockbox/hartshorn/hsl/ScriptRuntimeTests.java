package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.callable.module.InstanceNativeModule;
import org.dockbox.hartshorn.hsl.customizer.AbstractCodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import jakarta.inject.Inject;

@HartshornTest
@UseExpressionValidation
public class ScriptRuntimeTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> scripts() {
        final Path resources = Paths.get("src", "test", "resources");
        return Arrays.stream(resources.toFile().listFiles())
                .filter(file -> file.getName().endsWith(".hsl"))
                .map(file -> Result.of(() -> Files.readAllLines(file.toPath())).orNull())
                .filter(Objects::nonNull)
                .map(lines -> String.join("\n", lines))
                .map(Arguments::of);
    }

    public static Stream<Arguments> phases() {
        return Arrays.stream(Phase.values()).map(Arguments::of);
    }

    public static Stream<Arguments> bitwise() {
        return Stream.of(
                Arguments.of(TokenType.BITWISE_OR, 5, 7, 5 | 7),
                Arguments.of(TokenType.BITWISE_AND, 5, 7, 5 & 7),
                Arguments.of(TokenType.XOR, 5, 7, 5 ^ 7),
                Arguments.of(TokenType.SHIFT_LEFT, 5, 7, 5 << 7),
                Arguments.of(TokenType.SHIFT_RIGHT, 5, 7, 5 >> 7),
                Arguments.of(TokenType.LOGICAL_SHIFT_RIGHT, 5, 7, 5 >>> 7)
        );
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testPredefinedScript(final String content) {
        final StandardRuntime runtime = this.applicationContext.get(StandardRuntime.class);
        final ScriptContext context = Assertions.assertDoesNotThrow(() -> runtime.run(content));

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testPredefinedScriptWithOptionalSemicolons(final String content) {
        final String source = content.replaceAll(";", "");
        final StandardRuntime runtime = this.applicationContext.get(StandardRuntime.class);
        final ScriptContext context = Assertions.assertDoesNotThrow(() -> runtime.run(source));

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
    }

    @Test
    void testExpression() {
        final ValidateExpressionRuntime runtime = this.applicationContext.get(ValidateExpressionRuntime.class);
        final String expression = "1 == 1";
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
        Assertions.assertTrue(ValidateExpressionRuntime.valid(context));
    }

    @Test
    void testComplexExpression() {
        final ValidateExpressionRuntime runtime = this.applicationContext.get(ValidateExpressionRuntime.class);
        final String expression = "1 + 3 == 4 && 2 + 2 == 4 && 2 - 2 == 0";
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
        Assertions.assertTrue(ValidateExpressionRuntime.valid(context));
    }

    @Test
    void testExpressionWithGlobal() {
        final ValidateExpressionRuntime runtime = this.applicationContext.get(ValidateExpressionRuntime.class);
        runtime.global("a", 12);
        final String expression = "a == 12";
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
        Assertions.assertTrue(ValidateExpressionRuntime.valid(context));
    }

    @Test
    void testExpressionWithGlobalFunctionAccess() {
        final ValidateExpressionRuntime runtime = this.applicationContext.get(ValidateExpressionRuntime.class);
        runtime.global("context", this.applicationContext);
        final String expression = "context != null && context.log() != null";
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
        Assertions.assertTrue(ValidateExpressionRuntime.valid(context));
    }

    @Test
    void testScriptWithGlobalFunctionAccess() {
        final StandardRuntime runtime = this.applicationContext.get(StandardRuntime.class);
        runtime.global("context", this.applicationContext);
        final String expression = "context.log().info(\"Hello world!\")";
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
    }

    @Test
    void testExpressionWithNativeAccess() {
        final ValidateExpressionRuntime runtime = this.applicationContext.get(ValidateExpressionRuntime.class);
        final String expression = "log() != null";
        runtime.module("application", new InstanceNativeModule(this.applicationContext));
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
        Assertions.assertTrue(ValidateExpressionRuntime.valid(context));
    }

    @Test
    void testMultilineWithComments() {
        final StandardRuntime runtime = this.applicationContext.get(StandardRuntime.class);
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
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());

        final List<Comment> comments = context.comments();
        Assertions.assertEquals(3, comments.size());

        // Comments are not trimmed, so we need to include spaces in the expected result
        Assertions.assertEquals(" This is a comment", comments.get(0).text());
        Assertions.assertEquals(" This is also a comment, print(\"Hello world 3!\");", comments.get(1).text());
        Assertions.assertEquals(" This is a multi-line comment\nsee?!\n", comments.get(2).text());
    }

    @Test
    void testGlobalResultTracking() {
        final StandardRuntime runtime = this.applicationContext.get(StandardRuntime.class);
        final String expression = """
                var a = 12;
                var b = 13;
                var c = a + b;
                """;
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());

        final Map<String, Object> results = context.interpreter().global();
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(12d, results.get("a"));
        Assertions.assertEquals(13d, results.get("b"));
        Assertions.assertEquals(25d, results.get("c"));
    }

    @ParameterizedTest
    @MethodSource("phases")
    void testPhaseCustomizers(final Phase phase) {
        final StandardRuntime runtime = this.applicationContext.get(StandardRuntime.class);
        final String expression = "1 == 1";

        final AtomicBoolean called = new AtomicBoolean(false);
        final CodeCustomizer customizer = new AbstractCodeCustomizer(phase) {
            @Override
            public void call(final ScriptContext context) {
                called.set(true);
            }
        };
        runtime.customizer(customizer);

        runtime.run(expression);
        Assertions.assertTrue(called.get());
    }

    @ParameterizedTest
    @MethodSource("bitwise")
    void testBitwiseOperator(final TokenType token, final int left, final int right, final int expected) {
        final StandardRuntime runtime = this.applicationContext.get(StandardRuntime.class);
        final String expression = "var result = %s %s %s".formatted(left, token.representation(), right);
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());

        final Object result = context.interpreter().global().get("result");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testNegativeNumbers() {
        final ValidateExpressionRuntime runtime = this.applicationContext.get(ValidateExpressionRuntime.class);
        final String expression = "-1 == -1";
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
        Assertions.assertTrue(ValidateExpressionRuntime.valid(context));
    }

    @Test
    void testComplement() {
        final ValidateExpressionRuntime runtime = this.applicationContext.get(ValidateExpressionRuntime.class);
        final int expected = ~35; // -36
        final String expression = "~35 == %s".formatted(expected);
        final ScriptContext context = runtime.run(expression);

        Assertions.assertTrue(context.errors().isEmpty());
        Assertions.assertTrue(context.runtimeErrors().isEmpty());
        Assertions.assertTrue(ValidateExpressionRuntime.valid(context));
    }
}
