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

public interface CodeCustomizer {
    String tokenizing(String source, HslLexer lexer);

    List<Token> parsing(List<Token> source, Parser parser);

    List<Statement> resolving(List<Statement> statements, Resolver resolver, Map<String, NativeModule> modules);

    List<Statement> interpreting(List<Statement> statements, Interpreter interpreter);
}
