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

package org.dockbox.hartshorn.hsl.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ParserCustomizer;
import org.dockbox.hartshorn.hsl.ScriptComponentFactory;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.condition.ExpressionConditionContext;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.Customizer;

/**
 * Base implementation of {@link ScriptRuntime} that provides a default implementation for the
 * runtime phases. This class is designed to be extended by specific runtime implementations, such as
 * {@link ValidateExpressionRuntime}.
 *
 * <p>Implementations of this class should only have to provide the standard libraries and external
 * modules that are used by the runtime. The runtime itself is provided by this class.
 *
 * @see ScriptRuntime
 * @see ValidateExpressionRuntime
 * @see StandardRuntime
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class AbstractScriptRuntime extends ExpressionConditionContext implements MutableScriptRuntime {

    private final ScriptComponentFactory factory;
    private final ApplicationContext applicationContext;

    private ParserCustomizer parserCustomizer;

    protected AbstractScriptRuntime(
        ApplicationContext applicationContext,
        ScriptComponentFactory factory
    ) {
        this(applicationContext, factory, parser -> {
        });
    }

    protected AbstractScriptRuntime(
        ApplicationContext applicationContext,
        ScriptComponentFactory factory,
        ParserCustomizer parserCustomizer
    ) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.factory = factory;
        this.parserCustomizer = parserCustomizer;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    /**
     * Provides the standard libraries that are used by the runtime. These libraries are made
     * available to the script during the interpretation phase.
     *
     * @param context the context in which the libraries are used
     * @return the standard libraries
     */
    protected Map<String, NativeModule> standardLibraries(ScriptContext context) {
        return new HashMap<>();
    }

    @Override
    public ScriptContext interpret(String source) {
        return this.runUntil(source, Phase.INTERPRETING);
    }

    @Override
    public ScriptContext runUntil(String source, Phase until) {
        ScriptContext context = this.createScriptContext(source);
        return this.runUntil(context, until);
    }

    @Override
    public ScriptContext runUntil(ScriptContext context, Phase until) {
        try {
            // First phase always gets executed
            this.tokenize(context);
            if (until.ordinal() >= Phase.PARSING.ordinal()) {
                this.parse(context);
            }
            if (until.ordinal() >= Phase.RESOLVING.ordinal()) {
                this.resolve(context);
            }
            if (until.ordinal() >= Phase.INTERPRETING.ordinal()) {
                this.interpret(context);
            }
        }
        catch (ScriptEvaluationError e) {
            this.handleScriptEvaluationError(context, e);
        }
        return context;
    }

    @Override
    public ScriptContext runOnly(String source, Phase only) {
        ScriptContext context = this.createScriptContext(source);
        return this.runOnly(context, only);
    }

    @Override
    public ScriptContext runOnly(ScriptContext context, Phase only) {
        try {
            switch(only) {
            case TOKENIZING -> this.tokenize(context);
            case PARSING -> this.parse(context);
            case RESOLVING -> this.resolve(context);
            case INTERPRETING -> this.interpret(context);
            default -> throw new IllegalArgumentException("Unsupported standalone phase: " + only);
            }
        }
        catch (ScriptEvaluationError e) {
            this.handleScriptEvaluationError(context, e);
        }
        return context;
    }

    /**
     * Creates a new script context for the given source. The context is used to store the state of
     * the script's execution, and to provide access to the various executors that are used during
     * the execution.
     *
     * @param source the source code to execute
     * @return the context that was created during the execution
     */
    public ScriptContext createScriptContext(String source) {
        ScriptContext context = new ScriptContext(this, source);
        context.interpreter(this.createInterpreter(context));
        return context;
    }

    /**
     * Creates a new interpreter for the given context. The interpreter is used to evaluate the
     * script's statements and expressions.
     *
     * @param context the context in which the interpreter is used
     * @return the interpreter that was created
     */
    protected Interpreter createInterpreter(ScriptContext context) {
        Interpreter interpreter = this.factory.interpreter(context, this.standardLibraries(context), context.tokenRegistry(), this.applicationContext());
        interpreter.state().externalModules(this.externalModules());
        interpreter.executionOptions(this.interpreterOptions());
        return interpreter;
    }

    /**
     * Tokenizes the given source code, and stores the resulting tokens in the given context. Any
     * comments that are found in the source code are also stored in the context.
     *
     * @param context the context in which the tokenization is performed
     */
    protected void tokenize(ScriptContext context) {
        context.lexer(this.factory.lexer(context.tokenRegistry(), context.source()));
        this.customizePhase(Phase.TOKENIZING, context);
        List<Token> tokens = context.lexer().scanTokens();
        context.tokens(tokens);
        context.comments(context.lexer().comments());
    }

    /**
     * Parses the tokens that are stored in the given context, and stores the resulting statements in
     * the context.
     *
     * @param context the context in which the parsing is performed
     */
    protected void parse(ScriptContext context) {
        TokenParser parser = this.factory.parser(context.tokenRegistry(), context.tokens());
        this.parserCustomizer.configure(parser);

        context.parser(parser);
        this.customizePhase(Phase.PARSING, context);
        List<Statement> statements = context.parser().parse();
        context.statements(statements);
    }

    /**
     * Performs semantic analysis on the statements that are stored in the given context, preparing
     * any necessary resolution for the interpretation phase.
     *
     * @param context the context in which the resolution is performed
     */
    protected void resolve(ScriptContext context) {
        context.resolver(this.factory.resolver(context.interpreter()));
        context.interpreter().restore();
        this.customizePhase(Phase.RESOLVING, context);
        context.resolver().resolve(context.statements());
    }

    /**
     * Interprets the statements that are stored in the given context, and stores the results in the
     * interpreter's {@link Interpreter#resultCollector() result collector}.
     *
     * @param context the context in which the interpretation is performed
     */
    protected void interpret(ScriptContext context) {
        Interpreter interpreter = context.interpreter();
        // Interpreter modification is not allowed at this point, as it was restored before
        // the resolve phase.
        this.customizePhase(Phase.INTERPRETING, context);
        interpreter.state().global(this.globalVariables());
        interpreter.state().imports(this.imports());
        interpreter.interpret(context.statements());
    }

    /**
     * Customizes the given phase of the script's execution. This method is called for each of the
     * phases that are performed during the execution of a script.
     *
     * @param phase the phase that is being customized
     * @param context the context in which the customization is performed
     */
    protected void customizePhase(Phase phase, ScriptContext context) {
        for (CodeCustomizer customizer : this.customizers()) {
            if (customizer.phase() == phase) {
                customizer.call(context);
            }
        }
    }

    /**
     * Handles the given script evaluation error. This method is called when an error occurs during
     * the evaluation of a script, and allows for the cause of the error to be displayed in a more
     * user-friendly way.
     *
     * @param context the context in which the error occurred
     * @param error the error that occurred
     */
    protected void handleScriptEvaluationError(ScriptContext context, ScriptEvaluationError error) {
        String source = context.source();
        Phase phase = error.phase();
        int line = error.line();
        int column = error.column();

        StringBuilder sb = new StringBuilder();
        sb.append(error.getMessage());
        if (error.getMessage().trim().endsWith(".")) {
            sb.append(" While ");
        }
        else {
            sb.append(" while ");
        }
        sb.append(phase.name().toLowerCase(Locale.ROOT));
        if (line <= -1 || column <= -1) {
            sb.append(" (outside source).");
        }
        else {
            sb.append(" at line ");
            sb.append(line);
            sb.append(", column ");
            sb.append(column);
            sb.append(".");
        }

        String message = sb.toString();
        if (line > -1 && column > -1) {
            String[] lines = source.split("\n");
            String lineText = lines[line - 1];

            StringBuilder builder = new StringBuilder(" ".repeat(column+1));
            builder.setCharAt(column, '^');
            String marker = builder.toString();

            message = "%s\n%s\n%s".formatted(message, lineText, marker);
        }

        ScriptEvaluationError evaluationError = new ScriptEvaluationError(error, message, phase, error.at(), line, column);
        // We only want to customize the error message, not the stack trace, so we
        // keep the original stack trace.
        evaluationError.setStackTrace(evaluationError.getStackTrace());
        throw evaluationError;
    }

    @Override
    public void expressionParser(ASTNodeParser<? extends Expression> expressionParser) {
        this.parserCustomizer = this.parserCustomizer.compose(parser -> parser.expressionParser(expressionParser));
    }

    @Override
    public void statementParser(ASTNodeParser<? extends Statement> statementParser) {
        this.parserCustomizer = this.parserCustomizer.compose(parser -> parser.statementParser(statementParser));
    }

    @Override
    public void scriptContextCustomizer(Customizer<ScriptContext> customizer) {
        this.customizer(new CodeCustomizer() {

            @Override
            public Phase phase() {
                return Phase.TOKENIZING;
            }

            @Override
            public void call(ScriptContext context) {
                customizer.configure(context);
            }
        });
    }
}
