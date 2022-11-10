package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Set;

public interface TokenParser extends Context {

    TokenParser statementParser(ASTNodeParser<? extends Statement> parser);

    List<Statement> parse();

    boolean match(TokenType... types);

    Token find(TokenType... types);

    boolean check(TokenType... types);

    Token advance();

    boolean isAtEnd();

    Token peek();

    Token previous();

    Token consume(TokenType type, String message);

    Statement statement();

    ExpressionStatement expressionStatement();

    Expression expression();

    List<Statement> consume();

    <T extends Statement> Set<ASTNodeParser<T>> compatibleParsers(Class<T> type);

    <T extends Statement> Option<ASTNodeParser<T>> firstCompatibleParser(Class<T> type);
}
