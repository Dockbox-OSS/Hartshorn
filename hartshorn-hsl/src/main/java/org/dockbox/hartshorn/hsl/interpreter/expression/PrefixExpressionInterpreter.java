package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class PrefixExpressionInterpreter implements ASTNodeInterpreter<Object, PrefixExpression> {

    @Override
    public Object interpret(final PrefixExpression node, final InterpreterAdapter adapter) {
        final CallableNode value = (CallableNode) adapter.visitingScope().get(node.prefixOperatorName());
        final List<Object> args = new ArrayList<>();
        args.add(adapter.evaluate(node.rightExpression()));
        try {
            return value.call(node.prefixOperatorName(), adapter.interpreter(), null, args);
        }
        catch (final ApplicationException e) {
            throw new RuntimeError(node.prefixOperatorName(), e.getMessage());
        }
    }
}
