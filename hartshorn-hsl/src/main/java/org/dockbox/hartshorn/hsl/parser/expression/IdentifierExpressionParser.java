package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;

public class IdentifierExpressionParser implements ExpressionParser {
    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        if (parser.match(parser.tokenRegistry().literals().identifier())) {
            Token next = parser.peek();
            TokenTypePair array = parser.tokenRegistry().tokenPairs().array();
            if (next.type() == array.open()) {
                Token name = parser.previous();
                validator.expect(array.open());
                Expression index = parser.expression();
                validator.expect(array.close());
                return new ArrayGetExpression(name, index);
            }
            return new VariableExpression(parser.previous());
        }
        return chain.next(parser, validator);
    }

}
