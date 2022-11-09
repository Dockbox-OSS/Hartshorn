package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class LogicalExpressionParser implements ExpressionParser<Expression> {

    @Override
    public Option<Expression> parse(final TokenParser parser, final TokenStepValidator validator) {
        final Expression expression = parser.expression();
        if (parser.match(TokenType.OR, TokenType.XOR, TokenType.AND)) {
            final Token operator = parser.previous();
            final Expression right = parser.expression();
            return Option.of(new LogicalExpression(expression, parser.previous(), right));
        }
        return Option.of(expression);
    }

    @Override
    public Set<Class<? extends Expression>> types() {
        return Set.of(LogicalExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return false;
    }
}
