package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.objects.external.ExternalFunction;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

public class GetExpressionInterpreter implements ASTNodeInterpreter<Object, GetExpression> {

    @Override
    public Object interpret(final GetExpression node, final InterpreterAdapter adapter) {
        final Object object = adapter.evaluate(node.object());
        if (object instanceof PropertyContainer container) {
            Object result = container.get(node.name(), adapter.visitingScope(), adapter.interpreter().executionOptions());
            if (result instanceof ExternalObjectReference objectReference) result = objectReference.externalObject();
            if (result instanceof ExternalFunction bindableNode && object instanceof InstanceReference instance) {
                return bindableNode.bind(instance);
            }
            return result;
        }
        throw new RuntimeError(node.name(), "Only instances have properties.");
    }
}
