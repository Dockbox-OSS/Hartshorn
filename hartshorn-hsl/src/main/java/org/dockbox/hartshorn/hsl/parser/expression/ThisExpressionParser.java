package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class ThisExpressionParser implements ExpressionParser<ThisExpression> {

    @Override
    public Result<ThisExpression> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.THIS))
            return Result.of(new ThisExpression(parser.previous()));
        return Result.empty();
    }

    @Override
    public Set<Class<? extends ThisExpression>> types() {
        return Set.of(ThisExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return true;
    }
}
