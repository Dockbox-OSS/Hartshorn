package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.HslLanguageFactory;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.callable.module.StandardLibrary;
import org.dockbox.hartshorn.hsl.condition.ExpressionConditionContext;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

public class StandardRuntime extends ExpressionConditionContext {

    private final ApplicationContext applicationContext;
    private final HslLanguageFactory factory;

    @Inject
    public StandardRuntime(final ApplicationContext applicationContext, final HslLanguageFactory factory) {
        this.applicationContext = applicationContext;
        this.factory = factory;
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    protected Map<String, NativeModule> standardLibraries() {
        return StandardLibrary.asModules(this.applicationContext());
    }

    public ScriptContext run(final String source) {
        final ScriptContext context = new ScriptContext(source);
        context.interpreter(this.createInterpreter(context));

        this.tokenize(context);
        this.parse(context);

        // Stop if there was a syntax error.
        if (!context.errors().isEmpty()) return context;

        this.resolve(context);

        // Stop if there was a semantic error.
        if (!context.errors().isEmpty()) return context;

        // Start interpreter
        this.interpret(context);

        return context;
    }

    protected Interpreter createInterpreter(final ScriptContext context) {
        final Interpreter interpreter = this.factory.interpreter(context, context, context.logger(), this.standardLibraries());
        interpreter.externalModules(this.externalModules());
        return interpreter;
    }

    protected void tokenize(final ScriptContext context) {
        context.lexer(this.factory.lexer(context.source(), context));
        this.customizePhase(Phase.TOKENIZING, context);
        final List<Token> tokens = context.lexer().scanTokens();
        context.tokens(tokens);
        context.comments(context.lexer().comments());
    }

    protected void parse(final ScriptContext context) {
        context.parser(this.factory.parser(context.tokens(), context));
        this.customizePhase(Phase.PARSING, context);
        final List<Statement> statements = context.parser().parse();
        context.statements(statements);
    }

    protected void resolve(final ScriptContext context) {
        context.resolver(this.factory.resolver(context, context.interpreter()));
        context.interpreter().restore();
        this.customizePhase(Phase.RESOLVING, context);
        context.resolver().resolve(context.statements());
    }

    protected Map<String, Object> interpret(final ScriptContext context) {
        final Interpreter interpreter = context.interpreter();
        // Interpreter modification is not allowed at this point, as it was restored before
        // the resolve phase.
        this.customizePhase(Phase.INTERPRETING, context);
        interpreter.global(this.globalVariables());
        interpreter.imports(this.imports());
        interpreter.interpret(context.statements());

        return interpreter.global();
    }

    protected void customizePhase(final Phase phase, final ScriptContext context) {
        for (final CodeCustomizer customizer : this.customizers()) {
            if (customizer.phase() == phase)
                customizer.call(context);
        }
    }
}
