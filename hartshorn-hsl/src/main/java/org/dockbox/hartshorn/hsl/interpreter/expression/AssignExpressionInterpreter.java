package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;

public class AssignExpressionInterpreter implements ASTNodeInterpreter<Object, AssignExpression> {

    @Override
    public Object interpret(final AssignExpression node, final InterpreterAdapter adapter) {
        final Token name = node.name();
        final Object value = adapter.evaluate(node.value());

        final Integer distance = adapter.distance(node);
        if (distance != null) {
            adapter.visitingScope().assignAt(distance, name, value);
        }
        else {
            adapter.global().assign(name, value);
        }
        return value;
    }
}
