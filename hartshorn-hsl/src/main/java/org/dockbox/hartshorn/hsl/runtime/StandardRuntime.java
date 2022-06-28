package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.callable.module.StandardLibrary;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.lexer.HslLexer;
import org.dockbox.hartshorn.hsl.parser.Parser;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StandardRuntime implements ErrorReporter, ResultCollector {

    private static final String GLOBAL_RESULT = "$__result__$";

    protected final List<String> errors = new CopyOnWriteArrayList<>();
    protected final List<String> runtimeErrors = new CopyOnWriteArrayList<>();
    protected final List<Comment> comments = new CopyOnWriteArrayList<>();
    protected final Map<String, Object> results = new ConcurrentHashMap<>();


    protected final Map<String, Object> globalVariables = new ConcurrentHashMap<>();
    protected final Map<String, Class<?>> imports = new ConcurrentHashMap<>();


    protected final Interpreter interpreter;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName() + "/script" + this.hashCode());
    private final Set<CodeCustomizer> customizers = ConcurrentHashMap.newKeySet();
    private final ApplicationContext applicationContext;

    public StandardRuntime(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.interpreter = new Interpreter(this, this, this.logger, this.standardLibraries());
    }

    public void customizer(final CodeCustomizer customizer) {
        this.customizers.add(customizer);
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

    public void imports(final String name, final Class<?> type) {
        this.imports.put(name, type);
    }

    public void imports(final Class<?> type) {
        this.imports(type.getSimpleName(), type);
    }

    public void imports(final Map<String, Class<?>> imports) {
        this.imports.putAll(imports);
    }

    protected Map<String, NativeModule> standardLibraries() {
        return StandardLibrary.asModules(this.applicationContext());
    }

    public List<Comment> comments() {
        return this.comments;
    }

    public Map<String, Object> run(final String source) {
        this.reset();

        final List<Token> tokens = this.tokenize(source);
        List<Statement> statements = this.parse(tokens);

        // Stop if there was a syntax error.
        if (!this.errors.isEmpty()) return Map.of();

        statements = this.resolve(statements);

        // Stop if there was a semantic error.
        if (!this.errors.isEmpty()) return Map.of();

        // Start interpreter
        return this.interpret(statements);
    }

    private void reset() {
        this.errors.clear();
        this.runtimeErrors.clear();
        this.comments.clear();
        this.results.clear();
    }

    protected List<Token> tokenize(final String source) {
        final HslLexer lexer = new HslLexer(source, this);
        for (final CodeCustomizer customizer : this.customizers) {
            lexer.source(customizer.tokenizing(lexer.source(), lexer));
        }
        final List<Token> tokens = lexer.scanTokens();
        this.comments.addAll(lexer.comments());
        return tokens;
    }

    protected List<Statement> parse(final List<Token> tokens) {
        final Parser parser = new Parser(tokens, this);
        for (final CodeCustomizer customizer : this.customizers) {
            parser.tokens(customizer.parsing(parser.tokens(), parser));
        }
        return parser.parse();
    }

    protected List<Statement> resolve(final List<Statement> statements) {
        final Resolver resolver = new Resolver(this, this.interpreter);
        List<Statement> statementsToResolve = statements;
        for (final CodeCustomizer customizer : this.customizers) {
            statementsToResolve = customizer.resolving(statementsToResolve, resolver, this.interpreter.externalModules());
        }
        resolver.resolve(statementsToResolve);
        return statementsToResolve;
    }

    protected Map<String, Object> interpret(final List<Statement> statements) {
        List<Statement> statementsToInterpret = statements;
        for (final CodeCustomizer customizer : this.customizers) {
            statementsToInterpret = customizer.interpreting(statementsToInterpret, this.interpreter);
        }
        this.interpreter.global(this.globalVariables);
        this.interpreter.imports(this.imports);
        this.interpreter.interpret(statements);

        return this.interpreter.global();
    }

    public List<String> errors() {
        return this.errors;
    }

    public List<String> runtimeErrors() {
        return this.runtimeErrors;
    }

    @Override
    public void error(final Phase phase, final int line, final String message) {
        this.report(phase, line, "", message);
    }

    @Override
    public void error(final Phase phase, final Token token, final String message) {
        if (token.type() == TokenType.EOF) {
            this.report(phase, token.line(), " at end", message);
        }
        else {
            this.report(phase, token.line(), " at '" + token.lexeme() + "'", message);
        }
    }

    private void report(final Phase phase, final int line, final String where, final String message) {
        this.logger.warn("Error reported at line " + line + where + " while " + phase.name().toLowerCase(Locale.ROOT) + " script: " + message);
        this.errors.add(message);
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
