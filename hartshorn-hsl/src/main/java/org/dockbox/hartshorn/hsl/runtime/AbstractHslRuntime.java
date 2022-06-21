package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.ast.Statement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.lexer.HslLexer;
import org.dockbox.hartshorn.hsl.parser.Parser;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHslRuntime implements ErrorReporter {

    private final List<String> errors = new CopyOnWriteArrayList<>();
    private final List<String> runtimeErrors = new CopyOnWriteArrayList<>();
    private final Interpreter interpreter;

    public AbstractHslRuntime() {
        this.interpreter = new Interpreter(this, this.getLibraries());
    }

    protected abstract Map<String, Class<?>> getLibraries();

    public void run(final String source) {
        final HslLexer hslLexer = new HslLexer(source, this);
        final List<Token> tokens = hslLexer.scanTokens();
        final Parser parser = new Parser(tokens, this);
        final List<Statement> statements = parser.parse();

        // Stop if there was a syntax error.
        if (!this.errors.isEmpty()) return;

        final Resolver resolver = new Resolver(this, this.interpreter);
        resolver.resolve(statements);

        // Stop if there was a semantic error.
        if (!this.errors.isEmpty()) return;

        //Start HSL Interpreter
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
        } else {
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
        this.runtimeErrors.add(error.getMessage() +" \n[line " + error.getToken().line() + "]");
    }
}
