package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class UnaryExpressionParser implements ExpressionParser<UnaryExpression> {

    @Override
    public Result<UnaryExpression> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.BANG, TokenType.MINUS, TokenType.PLUS_PLUS, TokenType.MINUS_MINUS, TokenType.COMPLEMENT)) {
            final Token operator = parser.previous();
            final Expression right = parser.expression();
            return Result.of(new UnaryExpression(operator, right));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends UnaryExpression>> types() {
        return Set.of(UnaryExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return true;
    }
}
