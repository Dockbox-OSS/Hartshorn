package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

public class SetExpressionInterpreter implements ASTNodeInterpreter<Object, SetExpression> {

    @Override
    public Object interpret(final SetExpression node, final InterpreterAdapter adapter) {
        final Object object = adapter.evaluate(node.object());

        if (object instanceof PropertyContainer instance) {
            final Object value = adapter.evaluate(node.value());
            instance.set(node.name(), value, adapter.visitingScope(), adapter.interpreter().executionOptions());
            return value;
        }
        throw new RuntimeError(node.name(), "Only instances have properties.");
    }
}
