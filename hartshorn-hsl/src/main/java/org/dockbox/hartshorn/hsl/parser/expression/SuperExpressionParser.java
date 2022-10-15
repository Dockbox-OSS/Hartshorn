package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class SuperExpressionParser implements ExpressionParser<SuperExpression> {

    @Override
    public Result<SuperExpression> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.SUPER)) {
            final Token keyword = parser.previous();
            validator.expectAfter(TokenType.DOT, TokenType.SUPER);
            final Token method = validator.expect(TokenType.IDENTIFIER, "super class method name");
            return Result.of(new SuperExpression(keyword, method));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends SuperExpression>> types() {
        return Set.of(SuperExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return true;
    }
}
