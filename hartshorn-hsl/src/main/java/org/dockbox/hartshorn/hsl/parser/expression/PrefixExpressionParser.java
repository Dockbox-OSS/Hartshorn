package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;

public class PrefixExpressionParser extends AbstractFunctionOperatorExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        if (parser.check(parser.tokenRegistry().literals().identifier()) && this.hasPrefixFunction(parser, parser.peek())) {
            Token prefixFunctionName = parser.advance();
            Expression right = chain.next(parser, validator);
            return new PrefixExpression(prefixFunctionName, right);
        }
        else {
            return chain.next(parser, validator);
        }
    }

    protected boolean hasPrefixFunction(TokenParser parser, Token name) {
        return this.containedInFunctionContext(parser, context -> context.prefixFunctions().contains(name.lexeme()));
    }
}
