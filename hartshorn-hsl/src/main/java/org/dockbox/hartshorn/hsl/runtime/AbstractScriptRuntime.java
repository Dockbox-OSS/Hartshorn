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

package org.dockbox.hartshorn.hsl.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ScriptComponentFactory;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.condition.ExpressionConditionContext;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.token.Token;
import org.jetbrains.annotations.NotNull;

public class AbstractScriptRuntime extends ExpressionConditionContext implements ScriptRuntime {

    private final Set<ASTNodeParser<? extends Statement>> statementParsers;

    private final ApplicationContext applicationContext;
    private final ScriptComponentFactory factory;

    protected AbstractScriptRuntime(final ApplicationContext applicationContext, final ScriptComponentFactory factory) {
        this(applicationContext, factory, Set.of());
    }

    protected AbstractScriptRuntime(final ApplicationContext applicationContext, final ScriptComponentFactory factory,
                                    final Set<ASTNodeParser<? extends Statement>> statementParsers) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.factory = factory;
        this.statementParsers = statementParsers;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    protected Map<String, NativeModule> standardLibraries() {
        return new HashMap<>();
    }

    @Override
    public ScriptContext interpret(final String source) {
        return this.runUntil(source, Phase.INTERPRETING);
    }

    @Override
    public ScriptContext runUntil(final String source, final Phase until) {
        final ScriptContext context = this.createScriptContext(source);
        return this.runUntil(context, until);
    }

    @Override
    public ScriptContext runUntil(final ScriptContext context, final Phase until) {
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
        catch (final ScriptEvaluationError e) {
            this.handleScriptEvaluationError(context, e);
        }
        return context;
    }

    @Override
    public ScriptContext runOnly(final String source, final Phase only) {
        final ScriptContext context = this.createScriptContext(source);
        return this.runOnly(context, only);
    }

    @Override
    public ScriptContext runOnly(final ScriptContext context, final Phase only) {
        try {
            switch (only) {
                case TOKENIZING -> this.tokenize(context);
                case PARSING -> this.parse(context);
                case RESOLVING -> this.resolve(context);
                case INTERPRETING -> this.interpret(context);
                default -> throw new IllegalArgumentException("Unsupported standalone phase: " + only);
            }
        } catch (final ScriptEvaluationError e) {
            this.handleScriptEvaluationError(context, e);
        }
        return context;
    }

    @NotNull
    private ScriptContext createScriptContext(final String source) {
        final ScriptContext context = new ScriptContext(this, source);
        context.interpreter(this.createInterpreter(context));
        return context;
    }

    protected Interpreter createInterpreter(final ResultCollector resultCollector) {
        final Interpreter interpreter = this.factory.interpreter(resultCollector, this.standardLibraries(), this.applicationContext());
        interpreter.state().externalModules(this.externalModules());
        interpreter.executionOptions(this.interpreterOptions());
        return interpreter;
    }

    protected void tokenize(final ScriptContext context) {
        context.lexer(this.factory.lexer(context.source()));
        this.customizePhase(Phase.TOKENIZING, context);
        final List<Token> tokens = context.lexer().scanTokens();
        context.tokens(tokens);
        context.comments(context.lexer().comments());
    }

    protected void parse(final ScriptContext context) {
        final TokenParser parser = this.factory.parser(context.tokens());
        this.statementParsers.forEach(parser::statementParser);

        context.parser(parser);
        this.customizePhase(Phase.PARSING, context);
        final List<Statement> statements = context.parser().parse();
        context.statements(statements);
    }

    protected void resolve(final ScriptContext context) {
        context.resolver(this.factory.resolver(context.interpreter()));
        context.interpreter().restore();
        this.customizePhase(Phase.RESOLVING, context);
        context.resolver().resolve(context.statements());
    }

    protected void interpret(final ScriptContext context) {
        final Interpreter interpreter = context.interpreter();
        // Interpreter modification is not allowed at this point, as it was restored before
        // the resolve phase.
        this.customizePhase(Phase.INTERPRETING, context);
        interpreter.state().global(this.globalVariables());
        interpreter.state().imports(this.imports());
        interpreter.interpret(context.statements());
    }

    protected void customizePhase(final Phase phase, final ScriptContext context) {
        for (final CodeCustomizer customizer : this.customizers()) {
            if (customizer.phase() == phase) {
                customizer.call(context);
            }
        }
    }

    protected void handleScriptEvaluationError(final ScriptContext context, final ScriptEvaluationError error) {
        final String source = context.source();
        final Phase phase = error.phase();
        final int line = error.line();
        final int column = error.column();

        final StringBuilder sb = new StringBuilder();
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
            final String[] lines = source.split("\n");
            final String lineText = lines[line - 1];

            final StringBuilder builder = new StringBuilder(" ".repeat(column+1));
            builder.setCharAt(column, '^');
            final String marker = builder.toString();

            message = "%s\n%s\n%s".formatted(message, lineText, marker);
        }

        final ScriptEvaluationError evaluationError = new ScriptEvaluationError(error.getCause(), message, phase, error.at(), line, column);
        // We only want to customize the error message, not the stack trace, so we
        // keep the original stack trace.
        evaluationError.setStackTrace(evaluationError.getStackTrace());
        throw evaluationError;
    }
}
