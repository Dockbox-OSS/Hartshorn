package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;

public class ElvisExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);
        if (parser.match(ConditionTokenType.ELVIS)) {
            Token elvis = parser.previous();
            Expression rightExp = parser.expression();
            return new ElvisExpression(expression, elvis, rightExp);
        }
        return expression;
    }

}
