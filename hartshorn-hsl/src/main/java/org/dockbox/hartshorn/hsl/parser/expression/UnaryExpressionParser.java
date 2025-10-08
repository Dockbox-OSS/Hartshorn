package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class UnaryExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        if (parser.match(BaseTokenType.BANG, ArithmeticTokenType.MINUS, ArithmeticTokenType.PLUS_PLUS, ArithmeticTokenType.MINUS_MINUS, BitwiseTokenType.COMPLEMENT)) {
            Token operator = parser.previous();
            Expression right = chain.next(parser, validator);
            return new UnaryExpression(operator, right);
        }
        else {
            return chain.next(parser, validator);
        }
    }

}
