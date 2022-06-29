package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.parser.Parser;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The standard context which tracks the state of the execution of a script. It is used by the
 * {@link org.dockbox.hartshorn.hsl.runtime.StandardRuntime} to track the state of the script's
 * execution, and store its various executors. Everything but the original script can be
 * customized during the script's execution, though some runtimes may not support this.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class ScriptContext extends DefaultContext implements ErrorReporter, ResultCollector {

    private static final String GLOBAL_RESULT = "$__result__$";

    protected final List<String> errors = new CopyOnWriteArrayList<>();
    protected final List<String> runtimeErrors = new CopyOnWriteArrayList<>();
    protected final Map<String, Object> results = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName() + "/script" + this.hashCode());
    private final String source;

    private List<Token> tokens;
    private List<Statement> statements;
    private List<Comment> comments;

    private Lexer lexer;
    private Parser parser;
    private Resolver resolver;
    private Interpreter interpreter;

    public ScriptContext(final String source) {
        this.source = source;
    }

    public String source() {
        return this.source;
    }

    public List<Token> tokens() {
        return this.tokens;
    }

    public ScriptContext tokens(final List<Token> tokens) {
        this.tokens = tokens;
        return this;
    }

    public List<Statement> statements() {
        return this.statements;
    }

    public ScriptContext statements(final List<Statement> statements) {
        this.statements = statements;
        return this;
    }

    public List<Comment> comments() {
        return this.comments;
    }

    public ScriptContext comments(final List<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Lexer lexer() {
        return this.lexer;
    }

    public ScriptContext lexer(final Lexer lexer) {
        this.lexer = lexer;
        return this;
    }

    public Parser parser() {
        return this.parser;
    }

    public ScriptContext parser(final Parser parser) {
        this.parser = parser;
        return this;
    }

    public Resolver resolver() {
        return this.resolver;
    }

    public ScriptContext resolver(final Resolver resolver) {
        this.resolver = resolver;
        return this;
    }

    public Interpreter interpreter() {
        return this.interpreter;
    }

    public ScriptContext interpreter(final Interpreter interpreter) {
        this.interpreter = interpreter;
        return this;
    }

    public List<String> errors() {
        return this.errors;
    }

    public List<String> runtimeErrors() {
        return this.runtimeErrors;
    }

    public Logger logger() {
        return this.logger;
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

    @Override
    public void clear() {
        this.results.clear();
        this.errors.clear();
        this.runtimeErrors.clear();
    }
}
