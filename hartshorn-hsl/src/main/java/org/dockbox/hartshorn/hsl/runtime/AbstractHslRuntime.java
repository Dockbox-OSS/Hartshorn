package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.callable.NativeModule;
import org.dockbox.hartshorn.hsl.customizer.BeforeInterpretingCustomizer;
import org.dockbox.hartshorn.hsl.customizer.BeforeParsingCustomizer;
import org.dockbox.hartshorn.hsl.customizer.BeforeResolvingCustomizer;
import org.dockbox.hartshorn.hsl.customizer.BeforeTokenizingCustomizer;
import org.dockbox.hartshorn.hsl.customizer.HslCustomizer;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.lexer.HslLexer;
import org.dockbox.hartshorn.hsl.parser.Parser;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHslRuntime implements ErrorReporter, ResultCollector {

    private static final String GLOBAL_RESULT = "$__result__$";
    protected final List<String> errors = new CopyOnWriteArrayList<>();
    protected final List<String> runtimeErrors = new CopyOnWriteArrayList<>();
    protected final Map<String, Object> globalVariables = new ConcurrentHashMap<>();
    protected final Map<String, Object> results = new ConcurrentHashMap<>();
    protected final Interpreter interpreter;
    private final ApplicationContext applicationContext;

    protected final Set<BeforeTokenizingCustomizer> tokenizingCustomizers = ConcurrentHashMap.newKeySet();
    protected final Set<BeforeParsingCustomizer> parsingCustomizers = ConcurrentHashMap.newKeySet();
    protected final Set<BeforeResolvingCustomizer> resolvingCustomizers = ConcurrentHashMap.newKeySet();
    protected final Set<BeforeInterpretingCustomizer> interpretingCustomizers = ConcurrentHashMap.newKeySet();

    public AbstractHslRuntime(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.interpreter = new Interpreter(this, this, applicationContext.log(), this.standardLibraries());
    }

    public void customizer(final HslCustomizer customizer) {
        if (customizer instanceof BeforeTokenizingCustomizer tokenizingCustomizer)
            this.tokenizingCustomizers.add(tokenizingCustomizer);

        if (customizer instanceof BeforeParsingCustomizer parsingCustomizer)
            this.parsingCustomizers.add(parsingCustomizer);

        if (customizer instanceof BeforeResolvingCustomizer resolvingCustomizer)
            this.resolvingCustomizers.add(resolvingCustomizer);

        if (customizer instanceof BeforeInterpretingCustomizer interpretingCustomizer)
            this.interpretingCustomizers.add(interpretingCustomizer);
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    public void module(final String name, final NativeModule module) {
        this.interpreter.externalModule(name, module);
    }

    public void global(final String name, final Object value) {
        this.globalVariables.put(name, value);
    }

    public void global(final Map<String, Object> values) {
        this.globalVariables.putAll(values);
    }

    protected abstract Map<String, NativeModule> standardLibraries();

    public void run(final String source) {
        final List<Token> tokens = this.tokenize(source);
        List<Statement> statements = this.parse(tokens);

        // Stop if there was a syntax error.
        if (!this.errors.isEmpty()) return;

        statements = this.resolve(statements);

        // Stop if there was a semantic error.
        if (!this.errors.isEmpty()) return;

        //Start HSL Interpreter
        this.interpret(statements);
    }

    protected List<Token> tokenize(final String source) {
        final HslLexer lexer = new HslLexer(source, this);
        for (final BeforeTokenizingCustomizer customizer : this.tokenizingCustomizers) {
            lexer.source(customizer.customize(lexer.source(), lexer));
        }
        return lexer.scanTokens();
    }

    protected List<Statement> parse(final List<Token> tokens) {
        final Parser parser = new Parser(tokens, this);
        for (final BeforeParsingCustomizer customizer : this.parsingCustomizers) {
            parser.tokens(customizer.customize(parser.tokens(), parser));
        }
        return parser.parse();
    }

    protected List<Statement> resolve(final List<Statement> statements) {
        final Resolver resolver = new Resolver(this, this.interpreter);
        List<Statement> statementsToResolve = statements;
        for (final BeforeResolvingCustomizer customizer : this.resolvingCustomizers) {
            statementsToResolve = customizer.customize(statementsToResolve, resolver, this.interpreter.externalModules());
        }
        resolver.resolve(statementsToResolve);
        return statementsToResolve;
    }

    protected void interpret(final List<Statement> statements) {
        List<Statement> statementsToInterpret = statements;
        for (final BeforeInterpretingCustomizer customizer : this.interpretingCustomizers) {
            statementsToInterpret = customizer.customize(statementsToInterpret, this.interpreter);
        }
        this.interpreter.global(this.globalVariables);
        this.interpreter.interpret(statements);
    }

    public List<String> errors() {
        return this.errors;
    }

    public List<String> runtimeErrors() {
        return this.runtimeErrors;
    }

    @Override
    public void error(final int line, final String message) {
        this.report(line, "", message);
    }

    @Override
    public void error(final String message) {
        this.report(message);
    }

    @Override
    public void error(final Token token, final String message) {
        if (token.type() == TokenType.EOF) {
            this.report(token.line(), " at end", message);
        }
        else {
            this.report(token.line(), " at '" + token.lexeme() + "'", message);
        }
    }

    private void report(final int line, final String where, final String message) {
        this.errors.add("[line " + line + "] Error" + where + ": " + message);
    }

    private void report(final String message) {
        this.errors.add(message);
    }

    @Override
    public void runtimeError(final RuntimeError error) {
        this.runtimeErrors.add(error.getMessage() + " \n[line " + error.token().line() + "]");
    }

    @Override
    public void addResult(final Object value) {
        this.addResult(GLOBAL_RESULT, value);
    }

    @Override
    public void addResult(final String id, final Object value) {
        this.results.put(id, value);
    }

    @Override
    public <T> Result<T> result() {
        return this.result(GLOBAL_RESULT);
    }

    @Override
    public <T> Result<T> result(final String id) {
        return Result.of(this.results.get(id))
                .map(result -> (T) result);
    }
}
