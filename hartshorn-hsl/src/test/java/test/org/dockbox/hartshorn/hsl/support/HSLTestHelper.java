/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.hsl.support;

import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ExpressionScript;
import org.dockbox.hartshorn.hsl.ParserCustomizer;
import org.dockbox.hartshorn.hsl.StandardScriptComponentFactory;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.DefaultScriptStatementsParserCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.extension.ASTExtensionModule;
import org.dockbox.hartshorn.hsl.extension.ExpressionModule;
import org.dockbox.hartshorn.hsl.extension.RuntimeExtensionCodeCustomizer;
import org.dockbox.hartshorn.hsl.extension.StatementModule;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.DelegatingInterpreterVisitor;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterState;
import org.dockbox.hartshorn.hsl.interpreter.SimpleVisitorInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.modules.StandardUtilitiesLibrary;
import org.dockbox.hartshorn.hsl.objects.external.ExternalInstance;
import org.dockbox.hartshorn.hsl.parser.expression.ExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.StatementParser;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.SimpleScriptRuntime;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A helper class for testing HSL language features such as parsing and interpretation. This class
 * provides a fluent builder interface for configuring the test environment, including defining
 * variables, customizing parsers and extensions, and intercepting expression evaluation.
 *
 * <p>All tests executed through this helper are run in an isolated runtime, ensuring that each
 * test
 * has a clean environment.
 *
 * <p>Tests are always executed through a {@link SimpleScriptRuntime}, ensuring consistent behavior
 * across different test cases. This does not, however, include the standard library or any other
 * built-in modules or parsers, which must be explicitly added if required.
 *
 * <p>If you require the full standard runtime, you may use the
 * {@link DefaultScriptStatementsParserCustomizer} to enable all built-in parsers, and
 * {@link StandardUtilitiesLibrary} (through {@link InterpreterState#externalModules(Map)}) to add
 * the standard library modules.
 *
 * <p>Alternatively you may also opt to use {@link ExecutableScript} or {@link ExpressionScript}
 * directly, which both use the standard runtime by default, but lack the additional testing
 * features provided by this helper (though these may still be added manually).
 *
 * <h2>Built-in extensions</h2>
 * <h3>Checkpoints</h3>
 * <p>By default, all tests created through this helper will include the {@link CheckpointModule}
 * extension module, allowing for easy testing of checkpoints within scripts. Checkpoints can be
 * inserted using the {@code checkpoint} keyword followed by a unique descriptor:
 * {@code checkpoint("my-checkpoint")}.
 *
 * <p>After evaluating, checkpoints can be inspected using the {@link #checkpoints()} method.
 * During
 * interpretation, each time a checkpoint is reached, it is recorded along with the number of times
 * it has been accessed. The expression itself returns the number of times the checkpoint has been
 * reached so far, allowing it to be used safely within e.g. conditional loop structures.
 *
 * <h3>Captures</h3>
 * <p>For testing individual expressions, the {@link CaptureModule} extension module is provided
 * through the {@link #ofExpression(ApplicationContext, String)} builder method. This module allows
 * for wrapping expressions in a capture statement, which can then be easily extracted and inspected
 * using the {@link #expression()} method.
 *
 * <p>You can also manually add the module for more complex scripts, though note that capture
 * modules can only capture a single expression per script. If multiple expressions are captured,
 * the last one to be interpreted will be used. Note that this thus supports conditional execution
 * of captures, e.g. within branches or loops, but not multiple captures in sequence. Captures can
 * be added using {@code capture(expression)}.
 *
 * <p>Captured values are not unwrapped in any way, so if the expression returns e.g. an external
 * instance, the captured value will still be wrapped in a {@link ExternalInstance}.
 *
 * @since 0.7.0
 * 
 * @author Guus Lieben
 */
public class HSLTestHelper {

    private final ScriptContext context;
    private final RuntimeExtensionCodeCustomizer customizer;

    private HSLTestHelper(ScriptContext context, RuntimeExtensionCodeCustomizer customizer) {
        this.context = context;
        this.customizer = customizer;
    }

    /**
     * Creates a new HSL test helper builder for the given expression source. The source will be
     * wrapped in a capture statement to allow for easy extraction of the parsed expression.
     *
     * <p>Use this method when testing individual expressions in isolation. If you are testing
     * multiple expressions or full statements, use {@link #of(ApplicationContext, String)}
     * instead.
     *
     * @param applicationContext the application context
     * @param source the source code to parse
     *
     * @return the builder
     */
    public static Builder ofExpression(
        ApplicationContext applicationContext,
        String source
    ) {
        String captureSource = "%s(%s)".formatted(CaptureModule.CAPTURE.tokenName(), source);
        return of(applicationContext, captureSource).withCaptureModule();
    }

    /**
     * Creates a new HSL test helper builder for the given source.
     *
     * @param applicationContext the application context
     * @param source the source code to parse
     *
     * @return the builder
     */
    public static Builder of(ApplicationContext applicationContext, String source) {
        return new Builder(applicationContext, source)
            .extensions(extensions -> {
                extensions.expressionModules(new CheckpointModule());
            });
    }

    /**
     * Parses the current source into a list of statements.
     *
     * @return the parsed statements
     */
    public List<Statement> parse() {
        this.context.runtime().runUntil(this.context, Phase.PARSING);
        return this.context.statements();
    }

    /**
     * Parses the current source as a single expression wrapped in a capture statement. If the
     * source does not parse to exactly one statement, an assertion failure is raised.
     *
     * <p>Use this method when testing expression parsing and interpretation in isolation.
     *
     * @return the parsed expression
     */
    public Expression expression() {
        List<Statement> statements = this.parse();
        if (statements.size() != 1) {
            Assertions.fail("Expected exactly one statement but got " + statements.size());
        }
        Statement statement = statements.getFirst();
        CaptureModule.CaptureStatement captureStatement = Assertions.assertInstanceOf(
            CaptureModule.CaptureStatement.class,
            statement
        );
        return captureStatement.expression();
    }

    /**
     * Restricts the evaluation of expressions of the given type to the provided interpreter. This
     * may result in a copy being made of the current interpreter if it is not already a mock or
     * spy, so be aware of potential side effects.
     *
     * <p>A known side-effect is that if the current interpreter is a
     * {@link SimpleVisitorInterpreter}, the visitor will not be re-bound to the new spy interpreter
     * by default. This is worked around in this method, but may need to be adjusted for other
     * interpreter implementations.
     *
     * @param type the expression type to intercept
     * @param interpreter the interpreter to use for the given expression type
     * @param <T> the expression type
     *
     * @return this helper
     */
    public <T extends Expression> HSLTestHelper evaluateWith(
        Class<T> type,
        ASTNodeInterpreter<?, T> interpreter
    ) {
        Interpreter defaultInterpreter = this.context.interpreter();
        Interpreter spyInterpreter = MockUtil.isMock(defaultInterpreter)
            ? defaultInterpreter
            : Mockito.spy(defaultInterpreter);

        Mockito.doAnswer(invocation -> {
            T expression = invocation.getArgument(0);
            return interpreter.interpret(expression, spyInterpreter);
        }).when(spyInterpreter).evaluate(Mockito.any(type));

        if (spyInterpreter instanceof SimpleVisitorInterpreter visitorInterpreter) {
            // Re-bind the visitor to use the spy interpreter. The visitor implementation is a
            // record, and thus immutable, so we need to create a new one.
            DelegatingInterpreterVisitor visitor = new DelegatingInterpreterVisitor(spyInterpreter);
            Mockito.doAnswer(invocation -> visitor)
                .when(visitorInterpreter)
                .visitor();
        }

        this.context.interpreter(spyInterpreter);
        return this;
    }

    public Interpreter interpreter() {
        return this.context.interpreter();
    }

    public Resolver resolver() {
        return this.context.resolver();
    }

    public ScriptContext context() {
        return this.context;
    }

    public HSLTestHelper interpret() {
        this.context.runtime().runUntil(this.context, Phase.INTERPRETING);
        return this;
    }

    public HSLTestHelper interpretOnly() {
        this.context.runtime().runOnly(this.context, Phase.INTERPRETING);
        return this;
    }

    public Object findVariable(String name) {
        Map<String, Object> values = this.interpreter().visitingScope().values();
        return values.get(name);
    }

    public Object interpretValue() {
        return this.interpret()
            .captures()
            .capturedValue();
    }

    public CaptureModule captures() {
        return this.findExtensionModule(CaptureModule.class);
    }

    public CheckpointModule checkpoints() {
        return this.findExtensionModule(CheckpointModule.class);
    }

    public <T extends ASTExtensionModule<?, ?>> T findExtensionModule(Class<T> type) {
        Set<StatementModule<?>> statementModules = this.customizer.statementModules();
        Set<ExpressionModule<?>> expressionModules = this.customizer.expressionModules();
        for (StatementModule<?> module : statementModules) {
            if (type.isInstance(module)) {
                return type.cast(module);
            }
        }
        for (ExpressionModule<?> module : expressionModules) {
            if (type.isInstance(module)) {
                return type.cast(module);
            }
        }
        throw new IllegalStateException("No extension module of type " + type.getName() + " found");
    }

    public static class Builder {

        private final ApplicationContext applicationContext;
        private final String source;
        private final Map<String, Object> definedVariables = new HashMap<>();
        private final Map<String, NativeModule> modules = new HashMap<>();
        private final Set<CodeCustomizer> codeCustomizers = new HashSet<>();

        private Customizer<RuntimeExtensionCodeCustomizer> extensionCustomizer =
            Customizer.useDefaults();
        private ParserCustomizer parserCustomizer = parser -> {
        };

        private Builder(ApplicationContext applicationContext, String source) {
            this.applicationContext = applicationContext;
            this.source = source;
        }

        public Builder extensions(
            Customizer<RuntimeExtensionCodeCustomizer> extensionCustomizer
        ) {
            this.extensionCustomizer = extensionCustomizer.compose(this.extensionCustomizer);
            return this;
        }

        public Builder withCaptureModule() {
            return this.extensions(extensions -> extensions.statementModules(new CaptureModule()));
        }

        public Builder module(String name, NativeModule module) {
            this.modules.put(name, module);
            return this;
        }

        public Builder modules(Map<String, NativeModule> modules) {
            this.modules.putAll(modules);
            return this;
        }

        public Builder parser(ParserCustomizer parserCustomizer) {
            this.parserCustomizer = parserCustomizer.compose(this.parserCustomizer);
            return this;
        }

        /**
         * Adds an expression parser to the current parser configuration. Note that when adding
         * multiple parsers, the order in which they are added matters, as parsers are recursively
         * tried in the order they were added.
         *
         * <p>For the correct order of standard parsers, refer to the implementation of
         * {@link DefaultScriptStatementsParserCustomizer}.
         *
         * @param parser the expression parser to add
         *
         * @return this builder
         */
        public Builder expressionParser(ExpressionParser parser) {
            return this.parser(parsers -> parsers.expressionParser(parser));
        }

        /**
         * Adds a statement parser to the current parser configuration. Unlike expression parsers,
         * the order of statement parsers does not matter, as they are selected based on the current
         * token rather than being recursively tried.
         *
         * @param parser the statement parser to add
         *
         * @return this builder
         */
        public Builder statementParser(StatementParser<?> parser) {
            return this.parser(parsers -> parsers.statementParser(parser));
        }

        public Builder customize(CodeCustomizer codeCustomizer) {
            this.codeCustomizers.add(codeCustomizer);
            return this;
        }

        public Builder defineGlobal(String name, Object value) {
            return this.define(name, value, Interpreter::global);
        }

        public Builder defineLocal(String name, Object value) {
            return this.define(name, value, Interpreter::visitingScope);
        }

        public Builder define(
            String name,
            Object value,
            Function<Interpreter, VariableScope> scopeSelector
        ) {
            return this.customize(CodeCustomizer.of(Phase.INTERPRETING, context -> {
                Interpreter interpreter = context.interpreter();
                scopeSelector.apply(interpreter).define(name, value);
            }));
        }

        public HSLTestHelper build() {
            SimpleScriptRuntime runtime = new SimpleScriptRuntime(
                this.applicationContext,
                new StandardScriptComponentFactory(),
                this.parserCustomizer
            );
            runtime.modules(this.modules);

            runtime.customizers(this.codeCustomizers);

            RuntimeExtensionCodeCustomizer customizer = new RuntimeExtensionCodeCustomizer();
            this.extensionCustomizer.configure(customizer);
            runtime.customizer(customizer);

            ScriptContext context = runtime.createScriptContext(this.source);
            return new HSLTestHelper(context, customizer);
        }
    }
}
