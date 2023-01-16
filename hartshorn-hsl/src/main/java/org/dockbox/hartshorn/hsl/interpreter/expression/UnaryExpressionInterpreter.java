package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class UnaryExpressionInterpreter implements ASTNodeInterpreter<Object, UnaryExpression> {

    @Override
    public Object interpret(final UnaryExpression node, final InterpreterAdapter adapter) {
        final Object right = adapter.evaluate(node.rightExpression());
        final Object newValue = switch (node.operator().type()) {
            case MINUS -> {
                InterpreterUtilities.checkNumberOperand(node.operator(), right);
                yield -(double) right;
            }
            case PLUS_PLUS -> {
                InterpreterUtilities.checkNumberOperand(node.operator(), right);
                yield (double) right + 1;
            }
            case MINUS_MINUS -> {
                InterpreterUtilities.checkNumberOperand(node.operator(), right);
                yield (double) right - 1;
            }
            case BANG -> !InterpreterUtilities.isTruthy(right);
            case COMPLEMENT -> {
                InterpreterUtilities.checkNumberOperand(node.operator(), right);
                final int value = ((Double) right).intValue();
                // Cast to int is redundant, but required to suppress false-positive inspections.
                //noinspection RedundantCast
                yield (int) ~value;
            }
            default -> null;
        };
        if (node.rightExpression() instanceof VariableExpression variable) {
            adapter.visitingScope().assign(variable.name(), newValue);
        }
        return newValue;
    }
}
