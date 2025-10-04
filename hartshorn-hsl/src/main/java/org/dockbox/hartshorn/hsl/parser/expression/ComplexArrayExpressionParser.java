package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.hsl.token.type.LoopTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

import java.util.ArrayList;
import java.util.List;

public class ComplexArrayExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        if (parser.match(parser.tokenRegistry().tokenPairs().array().open())) {
            Token open = parser.previous();
            Expression expression = parser.expression();

            if (parser.match(parser.tokenRegistry().tokenPairs().array().close())) {
                List<Expression> elements = new ArrayList<>();
                elements.add(expression);
                return new ArrayLiteralExpression(open, parser.previous(), elements);
            } else if (parser.match(BaseTokenType.COMMA)) {
                return this.arrayLiteralExpression(parser, validator, open, expression);
            } else {
                return this.arrayComprehensionExpression(parser, validator, open, expression);
            }
        }
        return chain.next(parser, validator);
    }

    private ArrayLiteralExpression arrayLiteralExpression(
            TokenParser parser,
            TokenStepValidator validator,
            Token open,
            Expression expression
    ) {
        List<Expression> elements = new ArrayList<>();
        elements.add(expression);
        do {
            elements.add(parser.expression());
        }
        while (parser.match(BaseTokenType.COMMA));
        Token close = validator.expectAfter(parser.tokenRegistry().tokenPairs().array().close(), "array");
        return new ArrayLiteralExpression(open, close, elements);
    }

    private ArrayComprehensionExpression arrayComprehensionExpression(
            TokenParser parser,
            TokenStepValidator validator,
            Token open,
            Expression expression
    ) {
        Token forToken = validator.expectAfter(LoopTokenType.FOR, "expression");
        TokenType identifier = parser.tokenRegistry().literals().identifier();
        Token name = validator.expect(identifier, "variable name");

        Token inToken = validator.expectAfter(LoopTokenType.IN, "variable name");
        Expression iterable = parser.expression();

        Token ifToken = null;
        Expression condition = null;
        if (parser.match(ControlTokenType.IF)) {
            ifToken = parser.previous();
            condition = parser.expression();
        }

        Token elseToken = null;
        Expression elseExpression = null;
        if (parser.match(ControlTokenType.ELSE)) {
            elseToken = parser.previous();
            elseExpression = parser.expression();
        }

        Token close = validator.expectAfter(parser.tokenRegistry().tokenPairs().array().close(), "array");

        return new ArrayComprehensionExpression(
                iterable, expression,
                name, forToken, inToken,
                open, close,
                ifToken, condition,
                elseToken, elseExpression
        );
    }
}
