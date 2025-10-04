package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class BinaryEqualityExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {

    @Override
    protected TokenType[] whileMatching() {
        return new TokenType[] {
                ConditionTokenType.BANG_EQUAL,
                ConditionTokenType.EQUAL_EQUAL,
        };
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        return new BinaryExpression(expression, operator, right);
    }
}
