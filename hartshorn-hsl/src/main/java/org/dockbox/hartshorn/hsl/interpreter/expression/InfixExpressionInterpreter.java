package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class InfixExpressionInterpreter implements ASTNodeInterpreter<Object, InfixExpression> {

    @Override
    public Object interpret(final InfixExpression node, final InterpreterAdapter adapter) {
        final CallableNode value = (CallableNode) adapter.visitingScope().get(node.infixOperatorName());
        final List<Object> args = new ArrayList<>();
        args.add(adapter.evaluate(node.leftExpression()));
        args.add(adapter.evaluate(node.rightExpression()));

        try {
            return value.call(node.infixOperatorName(), adapter.interpreter(), null, args);
        }
        catch (final ApplicationException e) {
            throw new RuntimeError(node.infixOperatorName(), e.getMessage());
        }
    }
}
