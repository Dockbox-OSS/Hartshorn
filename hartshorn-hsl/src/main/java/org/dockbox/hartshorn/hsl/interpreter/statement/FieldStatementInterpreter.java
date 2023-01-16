package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.token.TokenType;

public class FieldStatementInterpreter implements ASTNodeInterpreter<Void, FieldStatement> {

    @Override
    public Void interpret(final FieldStatement node, final InterpreterAdapter adapter) {
        final Object value = adapter.evaluate(node.initializer());
        final int distance = adapter.distance(node.initializer());
        final PropertyContainer object = (PropertyContainer) adapter.visitingScope().getAt(node.name(), distance - 1, TokenType.THIS.representation());
        object.set(node.name(), value, adapter.visitingScope(), adapter.interpreter().executionOptions());
        return null;
    }
}
