/*
 * Copyright 2019-2023 the original author or authors.
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
import org.dockbox.hartshorn.hsl.HslExpression;
import org.dockbox.hartshorn.hsl.HslScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.customizer.AbstractCodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.modules.InstanceNativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.TokenType;
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
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseExpressionValidation
public class ScriptRuntimeTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> scripts() throws IOException {
        final Path resources = Paths.get("src", "test", "resources");
        final BiPredicate<Path, BasicFileAttributes> filter = (path, attributes) -> attributes.isRegularFile() && path.getFileName().toString().endsWith(".hsl");
        return Files.find(resources, 5, filter).map(Arguments::of);
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
    void testPredefinedScript(final Path path) throws IOException {
        this.assertNoErrorsReported(HslScript.of(this.applicationContext, path));
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testPredefinedScriptWithOptionalSemicolons(final Path path) throws IOException {
        final String source = HslScript.sourceFromPath(path).replaceAll(";", "");
        this.assertNoErrorsReported(source);
    }

    @Test
    void testExpression() {
        this.assertValid("1 == 1");
    }

    @Test
    void testComplexExpression() {
        final String expression = "1 + 3 == 4 && 2 + 2 == 4 && 2 - 2 == 0";
        this.assertValid(expression);
    }

    @Test
    void testExpressionWithGlobal() {
        final HslExpression expression = HslExpression.of(this.applicationContext, "a == 12");
        expression.runtime().global("a", 12);
        this.assertValid(expression);
    }

    @Test
    void testExpressionWithGlobalFunctionAccess() {
        final String expression = "context != null && context.log() != null";
        final HslExpression hslExpression = HslExpression.of(this.applicationContext, expression);
        hslExpression.runtime().global("context", this.applicationContext);
        this.assertValid(hslExpression);
    }

    @Test
    void testScriptWithGlobalFunctionAccess() {
        final String expression = "context.log().info(\"Hello world!\")";
        final HslScript script = HslScript.of(this.applicationContext, expression);
        script.runtime().global("context", this.applicationContext);
        this.assertNoErrorsReported(script);
    }

    @Test
    void testExpressionWithNativeAccess() {
        final HslExpression expression = HslExpression.of(this.applicationContext, "log() != null");
        expression.runtime().module("application", new InstanceNativeModule(this.applicationContext, this.applicationContext));
        this.assertValid(expression);
    }

    @Test
    void testMultilineWithComments() {
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
        final ScriptContext context = this.assertNoErrorsReported(expression);

        final List<Comment> comments = context.comments();
        Assertions.assertEquals(3, comments.size());

        // Comments are not trimmed, so we need to include spaces in the expected result
        Assertions.assertEquals(" This is a comment", comments.get(0).text());
        Assertions.assertEquals(" This is also a comment, print(\"Hello world 3!\");", comments.get(1).text());
        Assertions.assertEquals(" This is a multi-line comment\nsee?!\n", comments.get(2).text());
    }

    @Test
    void testGlobalResultTracking() {
        final String expression = """
                var a = 12;
                var b = 13;
                var c = a + b;
                """;
        final ScriptContext context = this.assertNoErrorsReported(expression);

        final Map<String, Object> results = context.interpreter().global().values();
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(12d, results.get("a"));
        Assertions.assertEquals(13d, results.get("b"));
        Assertions.assertEquals(25d, results.get("c"));
    }

    @ParameterizedTest
    @MethodSource("phases")
    void testPhaseCustomizers(final Phase phase) {
        final HslScript script = HslScript.of(this.applicationContext, "1 == 1");

        final AtomicBoolean called = new AtomicBoolean(false);
        final CodeCustomizer customizer = new AbstractCodeCustomizer(phase) {
            @Override
            public void call(final ScriptContext context) {
                called.set(true);
            }
        };
        script.runtime().customizer(customizer);
        script.evaluate();
        Assertions.assertTrue(called.get());
    }

    @ParameterizedTest
    @MethodSource("bitwise")
    void testBitwiseOperator(final TokenType token, final int left, final int right, final int expected) {
        final String expression = "var result = %s %s %s".formatted(left, token.representation(), right);
        final ScriptContext context = this.assertNoErrorsReported(expression);
        final Object result = context.interpreter().global().values().get("result");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testNegativeNumbers() {
        this.assertValid("-1 == -1");
    }

    @Test
    void testComplement() {
        final int expected = ~35; // -36
        final String expression = "~35 == %s".formatted(expected);
        this.assertValid(expression);
    }

    @Test
    void testInterpreterCanBeReused() {
        final HslScript script = HslScript.of(this.applicationContext, """
                var x = 1;
                test ("Variable has not been modified") {
                    return x == 1;
                }
                x = 2;
                """);
        script.evaluate();
        script.evaluate();
    }

    ScriptContext assertValid(final String expression) {
        final HslExpression hslExpression = HslExpression.of(this.applicationContext, expression);
        return this.assertValid(hslExpression);
    }

    ScriptContext assertValid(final HslExpression expression) {
        final ScriptContext context = Assertions.assertDoesNotThrow(expression::evaluate);
        Assertions.assertTrue(expression.valid(context));
        return context;
    }

    ScriptContext assertNoErrorsReported(final String expression) {
        final HslScript script = HslScript.of(this.applicationContext, expression);
        return this.assertNoErrorsReported(script);
    }

    ScriptContext assertNoErrorsReported(final HslScript script) {
        return Assertions.assertDoesNotThrow(script::evaluate);
    }
}
