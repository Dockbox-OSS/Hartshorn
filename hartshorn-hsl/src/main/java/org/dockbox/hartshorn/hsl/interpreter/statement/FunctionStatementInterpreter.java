package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;

public class FunctionStatementInterpreter implements ASTNodeInterpreter<Void, FunctionStatement> {

    @Override
    public Void interpret(final FunctionStatement node, final InterpreterAdapter adapter) {
        final VirtualFunction function = new VirtualFunction(node, adapter.visitingScope(), false);
        adapter.visitingScope().define(node.name().lexeme(), function);
        return null;
    }
}
