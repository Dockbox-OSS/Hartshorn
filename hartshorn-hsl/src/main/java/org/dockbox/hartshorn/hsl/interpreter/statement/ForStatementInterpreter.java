package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.ForStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class ForStatementInterpreter implements ASTNodeInterpreter<Void, ForStatement> {

    @Override
    public Void interpret(final ForStatement node, final InterpreterAdapter adapter) {
        adapter.withNextScope(() -> {
            adapter.execute(node.initializer());
            while (InterpreterUtilities.isTruthy(adapter.evaluate(node.condition()))) {
                try {
                    adapter.execute(node.body());
                }
                catch (final MoveKeyword moveKeyword) {
                    if (moveKeyword.moveType() == MoveKeyword.MoveType.BREAK) break;
                }
                adapter.execute(node.increment());
            }
        });
        return null;
    }
}
