package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class BitwiseExpressionParser implements ExpressionParser<Expression> {

    @Override
    public Option<Expression> parse(final TokenParser parser, final TokenStepValidator validator) {
        final Expression expression = parser.expression();
        if (parser.match(TokenType.SHIFT_LEFT,
                TokenType.SHIFT_RIGHT,
                TokenType.LOGICAL_SHIFT_RIGHT,
                TokenType.BITWISE_OR,
                TokenType.BITWISE_AND)) {
            final Token operator = parser.previous();
            final Expression right = parser.expression();
            return Option.of(new BitwiseExpression(expression, parser.previous(), right));
        }
        return Option.of(expression);
    }

    @Override
    public Set<Class<? extends Expression>> types() {
        return Set.of(BitwiseExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return false;
    }
}
