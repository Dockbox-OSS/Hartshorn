package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

import java.util.ArrayList;
import java.util.List;

public class CallExpressionParser implements ExpressionParser {

    private static final int MAX_NUM_OF_ARGUMENTS = 8;

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);
        if (expression != null) {
            TokenType identifier = parser.tokenRegistry().literals().identifier();
            while(true) {
                if(parser.match(parser.tokenRegistry().tokenPairs().parameters().open())) {
                    expression = this.finishCall(parser, validator, expression);
                }
                else if(parser.match(BaseTokenType.DOT)) {
                    Token name = parser.consume(identifier, "Expected property name after '.'.");
                    expression = new GetExpression(name, expression);
                }
                else if(parser.match(ArithmeticTokenType.PLUS_PLUS, ArithmeticTokenType.MINUS_MINUS)) {
                    Token operator = parser.previous();
                    expression = new PostfixExpression(operator, expression);
                }
                else {
                    break;
                }
            }
        }
        return expression;
    }

    private Expression finishCall(TokenParser parser, TokenStepValidator validator, Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        Token parenOpen = parser.previous();
        // For zero arguments
        if (!parser.check(parser.tokenRegistry().tokenPairs().parameters().close())) {
            do {
                if (arguments.size() >= MAX_NUM_OF_ARGUMENTS) {
                    throw ScriptEvaluationError.builder(Phase.PARSING)
                            .message(DiagnosticMessage.TOO_MANY_PARAMETERS, MAX_NUM_OF_ARGUMENTS)
                            .at(parser.peek())
                            .build();
                }
                arguments.add(parser.expression());
            }
            while (parser.match(BaseTokenType.COMMA));
        }
        Token parenClose = validator.expectAfter(parser.tokenRegistry().tokenPairs().parameters().close(), "arguments");
        return new FunctionCallExpression(callee, parenOpen, parenClose, arguments);
    }
}
