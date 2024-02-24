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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ExpressionScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.customizer.AbstractCodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.modules.InstanceNativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseExpressionValidation
public class ScriptRuntimeTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> scripts() throws IOException {
        Path resources = Paths.get("src", "test", "resources");
        BiPredicate<Path, BasicFileAttributes> filter = (path, attributes) -> attributes.isRegularFile() && path.getFileName().toString().endsWith(".hsl");
        return Files.find(resources, 5, filter).map(Arguments::of);
    }

    public static Stream<Arguments> phases() {
        return Arrays.stream(Phase.values()).map(Arguments::of);
    }

    public static Stream<Arguments> bitwise() {
        return Stream.of(
                Arguments.of(BitwiseTokenType.BITWISE_OR, 5, 7, 5 | 7),
                Arguments.of(BitwiseTokenType.BITWISE_AND, 5, 7, 5 & 7),
                Arguments.of(BitwiseTokenType.XOR, 5, 7, 5 ^ 7),
                Arguments.of(BitwiseTokenType.SHIFT_LEFT, 5, 7, 5 << 7),
                Arguments.of(BitwiseTokenType.SHIFT_RIGHT, 5, 7, 5 >> 7),
                Arguments.of(BitwiseTokenType.LOGICAL_SHIFT_RIGHT, 5, 7, 5 >>> 7)
        );
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testPredefinedScript(Path path) throws IOException {
        this.assertNoErrorsReported(ExecutableScript.of(this.applicationContext, path));
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testPredefinedScriptWithOptionalSemicolons(Path path) throws IOException {
        String source = ExecutableScript.sourceFromPath(path).replaceAll(";", "");
        this.assertNoErrorsReported(source);
    }

    @Test
    void testExpression() {
        this.assertValid("1 == 1");
    }

    @Test
    void testComplexExpression() {
        String expression = "1 + 3 == 4 && 2 + 2 == 4 && 2 - 2 == 0";
        this.assertValid(expression);
    }

    @Test
    void testExpressionWithGlobal() {
        ExpressionScript expression = ExpressionScript.of(this.applicationContext, "a == 12");
        expression.runtime().global("a", 12);
        this.assertValid(expression);
    }

    @Test
    void testExpressionWithGlobalFunctionAccess() {
        String expression = "context != null && context.environment() != null";
        ExpressionScript script = ExpressionScript.of(this.applicationContext, expression);
        script.runtime().global("context", this.applicationContext);
        this.assertValid(script);
    }

    @Test
    void testScriptWithGlobalFunctionAccess() {
        String expression = "context.environment().isBatchMode()";
        ExecutableScript script = ExecutableScript.of(this.applicationContext, expression);
        script.runtime().global("context", this.applicationContext);
        this.assertNoErrorsReported(script);
    }

    @Test
    void testExpressionWithNativeAccess() {
        ExpressionScript expression = ExpressionScript.of(this.applicationContext, "isClosed() == false");
        expression.runtime().module("application", new InstanceNativeModule(this.applicationContext, this.applicationContext));
        this.assertValid(expression);
    }

    @Test
    void testMultilineWithComments() {
        String expression = """
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
        ScriptContext context = this.assertNoErrorsReported(expression);

        List<Comment> comments = context.comments();
        Assertions.assertEquals(3, comments.size());

        // Comments are not trimmed, so we need to include spaces in the expected result
        Comment commentOne = comments.getFirst();
        Assertions.assertEquals(" This is a comment", commentOne.text());
        Assertions.assertEquals(3, commentOne.line());

        Comment commentTwo = comments.get(1);
        Assertions.assertEquals(" This is also a comment, print(\"Hello world 3!\");", commentTwo.text());
        Assertions.assertEquals(6, commentTwo.line());

        Comment commentThree = comments.get(2);
        Assertions.assertEquals(" This is a multi-line comment\nsee?!\n", commentThree.text());
        Assertions.assertEquals(9, commentThree.line());
    }

    @Test
    void testGlobalResultTracking() {
        String expression = """
                var a = 12;
                var b = 13;
                var c = a + b;
                """;
        ScriptContext context = this.assertNoErrorsReported(expression);

        Map<String, Object> results = context.interpreter().global().values();
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(12.0d, results.get("a"));
        Assertions.assertEquals(13.0d, results.get("b"));
        Assertions.assertEquals(25.0d, results.get("c"));
    }

    @ParameterizedTest
    @MethodSource("phases")
    void testPhaseCustomizers(Phase phase) {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, "1 == 1");

        AtomicBoolean called = new AtomicBoolean(false);
        CodeCustomizer customizer = new AbstractCodeCustomizer(phase) {
            @Override
            public void call(ScriptContext context) {
                called.set(true);
            }
        };
        script.runtime().customizer(customizer);
        script.evaluate();
        Assertions.assertTrue(called.get());
    }

    @ParameterizedTest
    @MethodSource("bitwise")
    void testBitwiseOperator(TokenType token, int left, int right, int expected) {
        String expression = "var result = %s %s %s".formatted(left, token.representation(), right);
        ScriptContext context = this.assertNoErrorsReported(expression);
        Object result = context.interpreter().global().values().get("result");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testNegativeNumbers() {
        this.assertValid("-1 == -1");
    }

    @Test
    void testComplement() {
        int expected = ~35; // -36
        String expression = "~35 == %s".formatted(expected);
        this.assertValid(expression);
    }

    @Test
    void testInterpreterCanBeReused() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                var x = 1;
                test ("Variable has not been modified") {
                    return x == 1;
                }
                x = 2;
                """);
        script.evaluate();
        script.evaluate();
    }

    ScriptContext assertValid(String expression) {
        ExpressionScript script = ExpressionScript.of(this.applicationContext, expression);
        return this.assertValid(script);
    }

    ScriptContext assertValid(ExpressionScript expression) {
        ScriptContext context = Assertions.assertDoesNotThrow(expression::evaluate);
        Assertions.assertTrue(ExpressionScript.valid(context));
        return context;
    }

    ScriptContext assertNoErrorsReported(String expression) {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, expression);
        return this.assertNoErrorsReported(script);
    }

    ScriptContext assertNoErrorsReported(ExecutableScript script) {
        return Assertions.assertDoesNotThrow(script::evaluate);
    }
}
