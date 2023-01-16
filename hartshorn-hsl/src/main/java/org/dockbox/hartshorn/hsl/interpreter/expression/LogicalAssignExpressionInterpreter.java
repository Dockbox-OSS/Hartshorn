package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.LogicalAssignExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

public class LogicalAssignExpressionInterpreter extends BitwiseInterpreter<Object, LogicalAssignExpression> {

    @Override
    public Object interpret(final LogicalAssignExpression node, final InterpreterAdapter adapter) {
        final Token name = node.name();
        final Object left = adapter.lookUpVariable(name, node);

        final Object right = adapter.evaluate(node.value());

        final Token op = node.assignmentOperator();
        final TokenType bitwiseOperator = node.logicalOperator();

        // Virtual token to indicate the position of the operator
        final Token token = new Token(bitwiseOperator, op.lexeme(), op.line(), op.column());
        final Object result = this.getBitwiseResult(token, left, right);

        final Integer distance = adapter.distance(node);
        if (distance != null) {
            adapter.visitingScope().assignAt(distance, name, result);
        }
        else {
            adapter.global().assign(name, result);
        }
        return result;
    }
}
