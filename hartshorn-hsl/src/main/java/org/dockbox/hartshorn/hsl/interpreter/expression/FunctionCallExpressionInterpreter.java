package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.objects.BindableNode;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallExpressionInterpreter implements ASTNodeInterpreter<Object, FunctionCallExpression> {

    @Override
    public Object interpret(final FunctionCallExpression node, final InterpreterAdapter adapter) {
        final Object callee = adapter.evaluate(node.callee());

        final List<Object> arguments = new ArrayList<>();
        for (final Expression argument : node.arguments()) {
            Object evaluated = adapter.evaluate(argument);
            if (evaluated instanceof ExternalObjectReference external) evaluated = external.externalObject();
            arguments.add(evaluated);
        }

        // Can't call non-callable nodes..
        if (!(callee instanceof final CallableNode function)) {
            throw new RuntimeError(node.openParenthesis(), "Can only call functions and classes, but received " + callee + ".");
        }

        try {
            if (callee instanceof InstanceReference instance) {
                return function.call(node.openParenthesis(), adapter.interpreter(), instance, arguments);
            }
            else if (callee instanceof BindableNode<?> bindable){
                return function.call(node.openParenthesis(), adapter.interpreter(), bindable.bound(), arguments);
            }
            else {
                return function.call(node.openParenthesis(), adapter.interpreter(), null, arguments);
            }
        }
        catch (final ApplicationException e) {
            throw new RuntimeError(node.openParenthesis(), e.getMessage());
        }
    }
}
