package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class ReturnStatementParser implements ASTNodeParser<ReturnStatement> {

    @Override
    public Option<ReturnStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.RETURN)) {
            final Token keyword = parser.previous();
            Expression value = null;
            if (!parser.check(TokenType.SEMICOLON)) {
                value = parser.expression();
            }
            validator.expectAfter(TokenType.SEMICOLON, "return value");
            return Option.of(new ReturnStatement(keyword, value));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends ReturnStatement>> types() {
        return Set.of(ReturnStatement.class);
    }
}
