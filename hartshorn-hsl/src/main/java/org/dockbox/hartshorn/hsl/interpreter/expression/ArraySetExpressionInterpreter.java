package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;

public class ArraySetExpressionInterpreter extends ArrayInterpreter<Object, ArraySetExpression> {

    @Override
    public Object interpret(final ArraySetExpression node, final InterpreterAdapter adapter) {
        return this.accessArray(adapter, node.name(), node.index(), (array, index) -> {
            final Object value = adapter.evaluate(node.value());
            array.value(value, index);
            return value;
        });
    }
}
