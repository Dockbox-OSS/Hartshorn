package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class BitwiseExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {

    @Override
    protected TokenType[] whileMatching() {
        return new TokenType[] {
                BitwiseTokenType.SHIFT_LEFT,
                BitwiseTokenType.SHIFT_RIGHT,
                BitwiseTokenType.LOGICAL_SHIFT_RIGHT,
                BitwiseTokenType.BITWISE_OR,
                BitwiseTokenType.BITWISE_AND,
        };
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        return new BitwiseExpression(expression, operator, right);
    }
}
