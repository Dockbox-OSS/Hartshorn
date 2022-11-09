package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class GroupingExpressionParser implements ExpressionParser<GroupingExpression> {

    @Override
    public Option<GroupingExpression> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.LEFT_PAREN)) {
            final Expression expr = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "expression");
            return Option.of(new GroupingExpression(expr));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends GroupingExpression>> types() {
        return Set.of(GroupingExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return true;
    }
}
