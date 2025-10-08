package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class AssignExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);

        if (parser.match(BaseTokenType.EQUAL)) {
            Token equals = parser.previous();
            Expression value = parser.expression();

            if (expression instanceof VariableExpression variableExpression) {
                Token name = variableExpression.name();
                return new AssignExpression(name, value);
            }
            else if (expression instanceof ArrayGetExpression arrayGetExpression) {
                Token name = arrayGetExpression.name();
                return new ArraySetExpression(name, arrayGetExpression.index(), value);
            }
            else if (expression instanceof GetExpression getExpression) {
                return new SetExpression(getExpression.object(), getExpression.name(), value);
            }
            throw ScriptEvaluationError.builder(Phase.PARSING)
                    .message(DiagnosticMessage.INVALID_ASSIGNMENT_TARGET, expression)
                    .at(equals)
                    .build();
        }
        return expression;
    }

}
