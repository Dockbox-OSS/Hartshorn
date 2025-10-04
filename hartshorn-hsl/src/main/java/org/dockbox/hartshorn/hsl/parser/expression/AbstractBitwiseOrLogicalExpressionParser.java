package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public abstract class AbstractBitwiseOrLogicalExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);
        while(parser.match(this.whileMatching())) {
            Token operator = parser.previous();
            Expression right = parser.expression();
            expression = this.define(expression, operator, right);
        }
        return expression;
    }

    protected abstract TokenType[] whileMatching();

    protected abstract Expression define(Expression expression, Token operator, Expression right);

}
