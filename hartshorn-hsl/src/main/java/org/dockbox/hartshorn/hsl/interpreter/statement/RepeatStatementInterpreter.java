package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;

public class RepeatStatementInterpreter implements ASTNodeInterpreter<Void, RepeatStatement> {

    @Override
    public Void interpret(final RepeatStatement node, final InterpreterAdapter adapter) {
        adapter.withNextScope(() -> {
            final Object value = adapter.evaluate(node.value());

            final boolean isNotNumber = !(value instanceof Number);

            if (isNotNumber) {
                throw new RuntimeException("Repeat Counter must be number");
            }

            final int counter = (int) Double.parseDouble(value.toString());
            for (int i = 0; i < counter; i++) {
                try {
                    adapter.execute(node.body());
                }
                catch (final MoveKeyword moveKeyword) {
                    if (moveKeyword.moveType() == MoveKeyword.MoveType.BREAK) break;
                }
            }
        });
        return null;
    }
}
