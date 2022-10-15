package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class GroupingExpressionParser implements ExpressionParser<GroupingExpression> {

    @Override
    public Result<GroupingExpression> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.LEFT_PAREN)) {
            final Expression expr = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "expression");
            return Result.of(new GroupingExpression(expr));
        }
        return Result.empty();
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
