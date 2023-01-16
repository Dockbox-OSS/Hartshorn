package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.RangeExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class RangeExpressionInterpreter implements ASTNodeInterpreter<Object, RangeExpression> {

    @Override
    public Object interpret(final RangeExpression node, final InterpreterAdapter adapter) {
        final Object start = InterpreterUtilities.unwrap(adapter.evaluate(node.leftExpression()));
        final Object end = InterpreterUtilities.unwrap(adapter.evaluate(node.rightExpression()));

        InterpreterUtilities.checkNumberOperands(node.operator(), start, end);

        final int min = ((Number) start).intValue();
        final int max = ((Number) end).intValue();

        final int length = max - min + 1;
        final Object[] result = new Object[length];
        for (int i = 0; i < length; i++) {
            result[i] = (double) min + i;
        }
        return new Array(result);
    }
}
