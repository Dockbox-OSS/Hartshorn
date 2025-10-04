package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class LogicalExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {

    @Override
    protected TokenType[] whileMatching() {
        return new TokenType[]{
                BitwiseTokenType.XOR,
                ConditionTokenType.OR,
                ConditionTokenType.AND,
        };
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        return new LogicalExpression(expression, operator, right);
    }
}
