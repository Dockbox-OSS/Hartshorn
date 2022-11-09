package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class IdentifierExpressionParser implements ExpressionParser<Expression> {

    @Override
    public Option<Expression> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.IDENTIFIER)) {
            final Token next = parser.peek();

            if (next.type() == TokenType.ARRAY_OPEN) {
                final Token name = parser.previous();

                validator.expect(TokenType.ARRAY_OPEN);
                final Expression index = parser.expression();

                validator.expect(TokenType.ARRAY_CLOSE);
                return Option.of(new ArrayGetExpression(name, index));
            }
            return Option.of(new VariableExpression(parser.previous()));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends Expression>> types() {
        return Set.of(VariableExpression.class, ArrayGetExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return true;
    }
}
