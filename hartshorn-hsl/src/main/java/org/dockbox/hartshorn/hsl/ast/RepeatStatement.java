package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class RepeatStatement extends Statement {

    private final Expression value;
    private final Statement loopBody;

    public RepeatStatement(final Expression value, final Statement loopBody) {
        this.value = value;
        this.loopBody = loopBody;
    }

    public Expression getValue() {
        return this.value;
    }

    public Statement getLoopBody() {
        return this.loopBody;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
