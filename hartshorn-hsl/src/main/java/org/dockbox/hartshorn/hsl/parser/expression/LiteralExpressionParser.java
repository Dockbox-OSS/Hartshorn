package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class LiteralExpressionParser implements ExpressionParser<LiteralExpression> {

    @Override
    public Option<LiteralExpression> parse(final TokenParser parser, final TokenStepValidator validator) {
        return Option.of(() -> {
            if (parser.match(TokenType.NUMBER, TokenType.STRING, TokenType.CHAR))
                return new LiteralExpression(parser.peek(), parser.previous().literal());
            else if (parser.match(TokenType.FALSE))
                return new LiteralExpression(parser.peek(), false);
            else if (parser.match(TokenType.TRUE))
                return new LiteralExpression(parser.peek(), true);
            else if (parser.match(TokenType.NULL))
                return new LiteralExpression(parser.peek(), null);
            else
                return null;
        });
    }

    @Override
    public Set<Class<? extends LiteralExpression>> types() {
        return Set.of(LiteralExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return true;
    }
}
