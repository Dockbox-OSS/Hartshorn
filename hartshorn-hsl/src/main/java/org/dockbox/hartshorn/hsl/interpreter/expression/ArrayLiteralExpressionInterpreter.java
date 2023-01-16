package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;

import java.util.ArrayList;
import java.util.List;

public class ArrayLiteralExpressionInterpreter implements ASTNodeInterpreter<Object, ArrayLiteralExpression> {

    @Override
    public Object interpret(final ArrayLiteralExpression node, final InterpreterAdapter adapter) {
        final List<Object> values = new ArrayList<>();
        for(final Expression expression : node.elements()) {
            final Object evaluate = adapter.evaluate(expression);
            values.add(evaluate);
        }
        return new Array(values.toArray());
    }
}
