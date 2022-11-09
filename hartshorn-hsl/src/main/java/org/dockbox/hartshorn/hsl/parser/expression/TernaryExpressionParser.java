package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class TernaryExpressionParser implements ExpressionParser<Expression> {

    @Override
    public Option<Expression> parse(final TokenParser parser, final TokenStepValidator validator) {
        final Expression expression = parser.expression();

        if (parser.match(TokenType.QUESTION_MARK)) {
            final Token question = parser.previous();
            final Expression firstExp = parser.expression();
            final Token colon = parser.peek();
            if (parser.match(TokenType.COLON)) {
                final Expression secondExp = parser.expression();
                return Option.of(new TernaryExpression(expression, question, firstExp, colon, secondExp));
            }
            throw new ScriptEvaluationError("Expected expression after " + TokenType.COLON.representation(), Phase.PARSING, colon);
        }

        return Option.of(expression);
    }

    @Override
    public Set<Class<? extends Expression>> types() {
        return Set.of(TernaryExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return false;
    }
}
