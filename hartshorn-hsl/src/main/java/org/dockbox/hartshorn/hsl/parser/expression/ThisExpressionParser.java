package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class ThisExpressionParser implements ExpressionParser<ThisExpression> {

    @Override
    public Option<ThisExpression> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.THIS))
            return Option.of(new ThisExpression(parser.previous()));
        return Option.empty();
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
