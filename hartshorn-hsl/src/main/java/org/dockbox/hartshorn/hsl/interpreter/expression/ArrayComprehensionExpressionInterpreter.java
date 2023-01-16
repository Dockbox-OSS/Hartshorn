package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

import java.util.ArrayList;
import java.util.List;

public class ArrayComprehensionExpressionInterpreter implements ASTNodeInterpreter<Object, ArrayComprehensionExpression> {

    @Override
    public Object interpret(final ArrayComprehensionExpression node, final InterpreterAdapter adapter) {
        final List<Object> values = new ArrayList<>();
        final Object collection = adapter.evaluate(node.collection());
        if (collection instanceof Iterable<?> iterable) {

            adapter.withNextScope(() -> {
                adapter.visitingScope().define(node.selector().lexeme(), null);
                adapter.withNextScope(() -> visitIterable(node, adapter, values, iterable));
            });
        }
        else {
            throw new RuntimeError(node.open(), "Collection must be iterable");
        }
        return new Array(values.toArray());
    }

    private static void visitIterable(final ArrayComprehensionExpression node, final InterpreterAdapter adapter,
                                      final List<Object> values, final Iterable<?> iterable) {
        for (final Object element : iterable) {
            adapter.visitingScope().assign(node.selector(), element);

            if (node.condition() != null) {
                final Object condition = adapter.evaluate(node.condition());
                if (!InterpreterUtilities.isTruthy(condition)) {
                    if (node.elseExpression() != null) {
                        final Object elseValue = adapter.evaluate(node.elseExpression());
                        values.add(elseValue);
                    }
                    continue;
                }
            }

            final Object result = adapter.evaluate(node.expression());
            values.add(result);
        }
    }
}
