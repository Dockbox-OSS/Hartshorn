package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;

public class TernaryExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);

        if (parser.match(BaseTokenType.QUESTION_MARK)) {
            Token question = parser.previous();
            Expression firstExp = parser.expression();
            Token colon = parser.peek();
            if (parser.match(BaseTokenType.COLON)) {
                Expression secondExp = parser.expression();
                return new TernaryExpression(expression, question, firstExp, colon, secondExp);
            }
            throw ScriptEvaluationError.builder(Phase.PARSING)
                    .message(DiagnosticMessage.EXPECTED_EXPRESSION_AFTER_X, BaseTokenType.COLON.representation())
                    .at(colon)
                    .build();
        }
        return expression;
    }

}
