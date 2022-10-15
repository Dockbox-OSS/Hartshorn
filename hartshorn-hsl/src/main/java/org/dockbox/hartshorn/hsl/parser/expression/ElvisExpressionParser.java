package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class ElvisExpressionParser implements ExpressionParser<Expression> {

    @Override
    public Result<Expression> parse(final TokenParser parser, final TokenStepValidator validator) {
        final Expression expr = parser.expression();
        if (parser.match(TokenType.ELVIS)) {
            final Token elvis = parser.previous();
            final Expression rightExp = parser.expression();
            return Result.of(new ElvisExpression(expr, elvis, rightExp));
        }
        return Result.of(expr);
    }

    @Override
    public Set<Class<? extends Expression>> types() {
        return Set.of(ElvisExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return false;
    }
}
