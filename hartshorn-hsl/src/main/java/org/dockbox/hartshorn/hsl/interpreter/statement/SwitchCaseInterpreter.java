package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;

public class SwitchCaseInterpreter implements ASTNodeInterpreter<Void, SwitchCase> {

    @Override
    public Void interpret(final SwitchCase node, final InterpreterAdapter adapter) {
        adapter.withNextScope(() -> {
            try {
                adapter.execute(node.body());
            } catch (final MoveKeyword moveKeyword) {
                if (moveKeyword.moveType() != MoveKeyword.MoveType.BREAK) {
                    throw new RuntimeException("Unexpected move keyword " + moveKeyword.moveType());
                }
            }
        });
        return null;
    }
}
