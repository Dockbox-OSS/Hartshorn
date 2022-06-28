package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.lexer.HslLexer;
import org.dockbox.hartshorn.hsl.parser.Parser;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;
import java.util.Map;

public class AbstractCodeCustomizer implements CodeCustomizer {
    @Override
    public String tokenizing(final String source, final HslLexer lexer) {
        return source;
    }

    @Override
    public List<Token> parsing(final List<Token> source, final Parser parser) {
        return source;
    }

    @Override
    public List<Statement> resolving(final List<Statement> statements, final Resolver resolver, final Map<String, NativeModule> modules) {
        return statements;
    }

    @Override
    public List<Statement> interpreting(final List<Statement> statements, final Interpreter interpreter) {
        return statements;
    }
}
