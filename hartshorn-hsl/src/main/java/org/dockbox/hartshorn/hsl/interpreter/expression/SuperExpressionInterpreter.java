package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.TokenType;

public class SuperExpressionInterpreter implements ASTNodeInterpreter<Object, SuperExpression> {

    @Override
    public Object interpret(final SuperExpression node, final InterpreterAdapter adapter) {
        final int distance = adapter.distance(node);
        final ClassReference superClass = (ClassReference) adapter.visitingScope().getAt(node.method(), distance, TokenType.SUPER.representation());
        final InstanceReference object = (InstanceReference) adapter.visitingScope().getAt(node.method(), distance - 1, TokenType.THIS.representation());
        final MethodReference method = superClass.method(node.method().lexeme());

        if (method == null) {
            throw new RuntimeError(node.method(), "Undefined property '" + node.method().lexeme() + "'.");
        }
        return method.bind(object);
    }
}
