package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

public class LogicalExpressionInterpreter extends BitwiseInterpreter<Object, LogicalExpression> {

    @Override
    public Object interpret(final LogicalExpression node, final InterpreterAdapter adapter) {
        final Object left = adapter.evaluate(node.leftExpression());
        switch (node.operator().type()) {
            case AND -> {
                if (!InterpreterUtilities.isTruthy(left)) {
                    return false;
                }
                // Don't evaluate right if left is not truthy
                final Object right = adapter.evaluate(node.rightExpression());
                return InterpreterUtilities.isTruthy(right);
            }
            case OR -> {
                if (InterpreterUtilities.isTruthy(left)) {
                    return true;
                }
                // No need to evaluate right if left is already truthy
                final Object right = adapter.evaluate(node.rightExpression());
                return InterpreterUtilities.isTruthy(right);
            }
            case XOR -> {
                final Object right = adapter.evaluate(node.rightExpression());
                return this.xor(left, right);
            }
            default -> throw new RuntimeError(node.operator(), "Unsupported logical operator.");
        }
    }
}
