package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class TernaryExpressionInterpreter implements ASTNodeInterpreter<Object, TernaryExpression> {

    @Override
    public Object interpret(final TernaryExpression node, final InterpreterAdapter adapter) {
        final Object condition = adapter.evaluate(node.condition());
        if (InterpreterUtilities.isTruthy(condition)) {
            return adapter.evaluate(node.firstExpression());
        }
        return adapter.evaluate(node.secondExpression());
    }
}
