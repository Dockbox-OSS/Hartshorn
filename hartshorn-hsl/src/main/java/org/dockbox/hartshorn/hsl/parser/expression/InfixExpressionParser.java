package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class InfixExpressionParser extends AbstractFunctionOperatorExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);

        TokenType identifier = parser.tokenRegistry().literals().identifier();
        while (parser.check(identifier) && this.hasInfixFunction(parser, parser.peek())) {
            Token operator = parser.advance();
            Expression right = parser.expression();
            expression = new InfixExpression(expression, operator, right);
        }
        return expression;
    }

    private boolean hasInfixFunction(TokenParser parser, Token name) {
        return this.containedInFunctionContext(parser, context -> context.infixFunctions().contains(name.lexeme()));
    }
}
