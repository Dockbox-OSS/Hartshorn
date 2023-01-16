package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;

public class ArrayGetExpressionInterpreter extends ArrayInterpreter<Object, ArrayGetExpression> {

    @Override
    public Object interpret(final ArrayGetExpression node, final InterpreterAdapter adapter) {
        return this.accessArray(adapter, node.name(), node.index(), Array::value);
    }
}
