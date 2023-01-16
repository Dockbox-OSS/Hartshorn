package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;

public class BitwiseExpressionInterpreter extends BitwiseInterpreter<Object, BitwiseExpression> {

    @Override
    public Object interpret(final BitwiseExpression node, final InterpreterAdapter adapter) {
        final Object left = adapter.evaluate(node.leftExpression());
        final Object right = adapter.evaluate(node.rightExpression());
        return this.getBitwiseResult(node.operator(), left, right);
    }
}
