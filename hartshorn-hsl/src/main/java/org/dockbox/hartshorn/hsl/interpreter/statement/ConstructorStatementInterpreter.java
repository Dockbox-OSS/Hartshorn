package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;

public class ConstructorStatementInterpreter implements ASTNodeInterpreter<Void, ConstructorStatement> {

    @Override
    public Void interpret(final ConstructorStatement node, final InterpreterAdapter adapter) {
        final VirtualFunction function = new VirtualFunction(node, adapter.visitingScope(), true);
        adapter.visitingScope().define(node.initializerIdentifier().lexeme(), function);
        return null;
    }
}
