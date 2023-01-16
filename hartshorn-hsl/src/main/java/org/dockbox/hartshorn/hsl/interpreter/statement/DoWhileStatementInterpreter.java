package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class DoWhileStatementInterpreter implements ASTNodeInterpreter<Void, DoWhileStatement> {

    @Override
    public Void interpret(final DoWhileStatement node, final InterpreterAdapter adapter) {
        adapter.withNextScope(() -> {
            do {
                try {
                    adapter.execute(node.body());
                }
                catch (final MoveKeyword moveKeyword) {
                    if (moveKeyword.moveType() == MoveKeyword.MoveType.BREAK) break;
                }
            }
            while (InterpreterUtilities.isTruthy(adapter.evaluate(node.condition())));
        });
        return null;
    }
}
