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

import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ExpressionScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.modules.InstanceNativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
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

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
public class ScriptRuntimeTests {

    @Inject
    private ApplicationContext applicationContext;

    @SuppressWarnings("StreamResourceLeak")
    public static Stream<Arguments> scripts() throws IOException {
        Path resources = Paths.get("src", "test", "resources");
        BiPredicate<Path, BasicFileAttributes> filter =
            (path, attributes) -> attributes.isRegularFile() && path.getFileName()
                .toString()
                .endsWith(".hsl");
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
    void predefinedScript(Path path) throws Exception {
        this.assertNoErrorsReported(ExecutableScript.of(this.applicationContext, path));
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void predefinedScriptWithOptionalSemicolons(Path path) throws Exception {
        String source = ExecutableScript.sourceFromPath(path).replaceAll(";", "");
        this.assertNoErrorsReported(source);
    }

    @Test
    void expression() {
        this.assertValid("1 == 1");
    }

    @Test
    void complexExpression() {
        String expression = "1 + 3 == 4 && 2 + 2 == 4 && 2 - 2 == 0";
        this.assertValid(expression);
    }

    @Test
    void expressionWithGlobal() {
        ExpressionScript expression = ExpressionScript.of(this.applicationContext, "a == 12");
        expression.runtime().global("a", 12);
        this.assertValid(expression);
    }

    @Test
    void expressionWithGlobalFunctionAccess() {
        String expression = "context != null && context.environment() != null";
        ExpressionScript script = ExpressionScript.of(this.applicationContext, expression);
        script.runtime().global("context", this.applicationContext);
        this.assertValid(script);
    }

    @Test
    void scriptWithGlobalFunctionAccess() {
        String expression = "context.environment().configuration().isBatchMode()";
        ExecutableScript script = ExecutableScript.of(this.applicationContext, expression);
        script.runtime().global("context", this.applicationContext);
        this.assertNoErrorsReported(script);
    }

    @Test
    void expressionWithNativeAccess() {
        ExpressionScript expression =
            ExpressionScript.of(this.applicationContext, "isClosed() == false");
        expression.runtime()
            .module("application",
                new InstanceNativeModule(this.applicationContext, this.applicationContext));
        this.assertValid(expression);
    }

    @Test
    void multilineWithComments() {
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
        assertThat(comments).hasSize(3);

        // Comments are not trimmed, so we need to include spaces in the expected result
        Comment commentOne = comments.getFirst();
        assertThat(commentOne.text()).isEqualTo(" This is a comment");
        assertThat(commentOne.line()).isEqualTo(3);

        Comment commentTwo = comments.get(1);
        assertThat(commentTwo.text()).isEqualTo(" This is also a comment, print(\"Hello world 3!\");");
        assertThat(commentTwo.line()).isEqualTo(6);

        Comment commentThree = comments.get(2);
        assertThat(commentThree.text()).isEqualTo(" This is a multi-line comment\nsee?!\n");
        assertThat(commentThree.line()).isEqualTo(9);
    }

    @Test
    void globalResultTracking() {
        String expression = """
            var a = 12;
            var b = 13;
            var c = a + b;
            """;
        ScriptContext context = this.assertNoErrorsReported(expression);

        Map<String, Object> results = context.interpreter().global().values();
        assertThat(results)
                .containsEntry("a", 12.0d)
                .containsEntry("b", 13.0d)
                .containsEntry("c", 25.0d);
    }

    @ParameterizedTest
    @MethodSource("phases")
    void phaseCustomizers(Phase phase) {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, "1 == 1");

        AtomicBoolean called = new AtomicBoolean(false);
        CodeCustomizer customizer = CodeCustomizer.of(phase, context -> called.set(true));
        script.runtime().customizer(customizer);
        script.evaluate();
        assertThat(called.get()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("bitwise")
    void bitwiseOperator(TokenType token, int left, int right, int expected) {
        String expression = "var result = %s %s %s".formatted(left, token.representation(), right);
        ScriptContext context = this.assertNoErrorsReported(expression);
        Object result = context.interpreter().global().values().get("result");
        assertThat(result)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void negativeNumbers() {
        this.assertValid("-1 == -1");
    }

    @Test
    void complement() {
        int expected = ~35; // -36
        String expression = "~35 == %s".formatted(expected);
        this.assertValid(expression);
    }

    @Test
    void interpreterCanBeReused() {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
            var x = 1;
            test ("Variable has not been modified") {
                yield x == 1;
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
        ScriptContext context = expression.evaluate();
        assertThat(ExpressionScript.valid(context)).isTrue();
        return context;
    }

    ScriptContext assertNoErrorsReported(String expression) {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, expression);
        return this.assertNoErrorsReported(script);
    }

    ScriptContext assertNoErrorsReported(ExecutableScript script) {
        return script.evaluate();
    }
}
