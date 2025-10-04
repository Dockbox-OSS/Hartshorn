package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;

public class GroupingExpressionParser implements ExpressionParser {
    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        if (parser.match(parser.tokenRegistry().tokenPairs().parameters().open())) {
            Expression expression = parser.expression();
            validator.expectAfter(parser.tokenRegistry().tokenPairs().parameters().close(), "expression");
            return new GroupingExpression(expression);
        }
        return chain.next(parser, validator);
    }

}
