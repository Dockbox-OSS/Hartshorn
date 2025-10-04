package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.RangeExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LoopTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class RangeExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {
    @Override
    protected TokenType[] whileMatching() {
        return new TokenType[] {
                LoopTokenType.RANGE,
        };
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        return new RangeExpression(expression, operator, right);
    }
}
