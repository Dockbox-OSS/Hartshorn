package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalAssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class LogicalAssignExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {

    private final TokenType[] assignmentTokens;

    public LogicalAssignExpressionParser(TokenRegistry registry) {
        this.assignmentTokens = registry
                .tokenTypes(token -> token.assignsWith() != null)
                .toArray(TokenType[]::new);
    }

    @Override
    protected TokenType[] whileMatching() {
        return this.assignmentTokens;
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        if (expression instanceof VariableExpression variable) {
            return new LogicalAssignExpression(variable.name(), operator, right);
        }
        else {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                    .message(DiagnosticMessage.INVALID_ASSIGNMENT_TARGET, expression)
                    .at(operator)
                    .build();
        }
    }
}
