package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

public class PostfixExpressionInterpreter implements ASTNodeInterpreter<Object, PostfixExpression> {

    @Override
    public Object interpret(final PostfixExpression node, final InterpreterAdapter adapter) {
        final Object left = adapter.evaluate(node.leftExpression());
        InterpreterUtilities.checkNumberOperand(node.operator(), left);

        final double newValue = switch (node.operator().type()) {
            case PLUS_PLUS -> (double) left + 1;
            case MINUS_MINUS -> (double) left -1;
            default -> throw new RuntimeError(node.operator(), "Invalid postfix operator " + node.operator().type());
        };

        if (node.leftExpression() instanceof VariableExpression variable) {
            adapter.visitingScope().assign(variable.name(), newValue);
        }
        return left;
    }
}
