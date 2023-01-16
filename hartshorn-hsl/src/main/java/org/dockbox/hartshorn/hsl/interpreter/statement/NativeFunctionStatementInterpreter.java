package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.modules.HslLibrary;

public class NativeFunctionStatementInterpreter implements ASTNodeInterpreter<Void, NativeFunctionStatement> {

    @Override
    public Void interpret(final NativeFunctionStatement node, final InterpreterAdapter adapter) {
        final HslLibrary hslLibrary = new HslLibrary(node, adapter.interpreter().state().externalModules());
        adapter.visitingScope().define(node.name().lexeme(), hslLibrary);
        return null;
    }
}
