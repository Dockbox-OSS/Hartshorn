package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class ElvisExpressionInterpreter implements ASTNodeInterpreter<Object, ElvisExpression> {

    @Override
    public Object interpret(final ElvisExpression node, final InterpreterAdapter adapter) {
        final Object condition = adapter.evaluate(node.condition());
        if (InterpreterUtilities.isTruthy(condition)) {
            return condition;
        }
        return adapter.evaluate(node.rightExpression());
    }
}
