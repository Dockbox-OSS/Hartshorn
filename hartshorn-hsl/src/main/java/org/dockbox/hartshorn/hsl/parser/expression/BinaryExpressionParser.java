package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class BinaryExpressionParser implements ExpressionParser<Expression> {

    @Override
    public Option<Expression> parse(final TokenParser parser, final TokenStepValidator validator) {
        final Expression expression = parser.expression();
        if (parser.match(
                TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL,
                TokenType.SLASH, TokenType.STAR,
                TokenType.MINUS, TokenType.PLUS,
                TokenType.GREATER, TokenType.LESS,
                TokenType.GREATER_EQUAL, TokenType.LESS_EQUAL,
                TokenType.MODULO)) {
            final Token operator = parser.previous();
            final Expression right = parser.expression();
            return Option.of(new BinaryExpression(expression, parser.previous(), right));
        }
        return Option.of(expression);
    }

    @Override
    public Set<Class<? extends Expression>> types() {
        return Set.of(BinaryExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return false;
    }
}
