package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class BinaryAdditionExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {

    @Override
    protected TokenType[] whileMatching() {
        return new TokenType[] {
                ArithmeticTokenType.MINUS,
                ArithmeticTokenType.PLUS,
        };
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        return new BinaryExpression(expression, operator, right);
    }
}
