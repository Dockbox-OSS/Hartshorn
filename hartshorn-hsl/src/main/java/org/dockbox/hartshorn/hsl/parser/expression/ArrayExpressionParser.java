package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArrayExpressionParser implements ExpressionParser<Expression> {

    @Override
    public Option<Expression> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.ARRAY_OPEN)) {
            final Token open = parser.previous();
            final Expression expr = parser.expression();

            if (parser.match(TokenType.ARRAY_CLOSE)) {
                final List<Expression> elements = new ArrayList<>();
                elements.add(expr);
                return Option.of(new ArrayLiteralExpression(open, parser.previous(), elements));
            }
            else if (parser.match(TokenType.COMMA)) return Option.of(this.arrayLiteralExpression(parser, validator, open, expr));
            else return Option.of(this.arrayComprehensionExpression(parser, validator, open, expr));
        }
        return Option.empty();
    }

    private ArrayLiteralExpression arrayLiteralExpression(final TokenParser parser, final TokenStepValidator validator, final Token open, final Expression expr) {
        final List<Expression> elements = new ArrayList<>();
        elements.add(expr);
        do {
            elements.add(parser.expression());
        }
        while (parser.match(TokenType.COMMA));
        final Token close = validator.expectAfter(TokenType.ARRAY_CLOSE, "array");
        return new ArrayLiteralExpression(open, close, elements);
    }

    private ArrayComprehensionExpression arrayComprehensionExpression(final TokenParser parser, final TokenStepValidator validator, final Token open, final Expression expr) {
        final Token forToken = validator.expectAfter(TokenType.FOR, "expression");
        final Token name = validator.expect(TokenType.IDENTIFIER, "variable name");

        final Token inToken = validator.expectAfter(TokenType.IN, "variable name");
        final Expression iterable = parser.expression();

        Token ifToken = null;
        Expression condition = null;
        if (parser.match(TokenType.IF)) {
            ifToken = parser.previous();
            condition = parser.expression();
        }

        Token elseToken = null;
        Expression elseExpr = null;
        if (parser.match(TokenType.ELSE)) {
            elseToken = parser.previous();
            elseExpr = parser.expression();
        }

        final Token close = validator.expectAfter(TokenType.ARRAY_CLOSE, "array");

        return new ArrayComprehensionExpression(iterable, expr, name, forToken, inToken, open, close, ifToken, condition, elseToken, elseExpr);
    }

    @Override
    public Set<Class<? extends Expression>> types() {
        return Set.of(ArrayLiteralExpression.class, ArrayComprehensionExpression.class);
    }

    @Override
    public boolean isValueExpression() {
        return true;
    }
}
