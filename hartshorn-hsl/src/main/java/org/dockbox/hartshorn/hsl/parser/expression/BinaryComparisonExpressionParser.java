package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class BinaryComparisonExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {

    @Override
    protected TokenType[] whileMatching() {
        return new TokenType[]{
                ConditionTokenType.GREATER,
                ConditionTokenType.GREATER_EQUAL,
                ConditionTokenType.LESS,
                ConditionTokenType.LESS_EQUAL,
        };
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        return new BinaryExpression(expression, operator, right);
    }
}
