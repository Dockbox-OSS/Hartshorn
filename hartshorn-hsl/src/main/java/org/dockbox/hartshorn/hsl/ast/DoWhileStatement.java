package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class DoWhileStatement extends WhileStatement {

    private Expression condition;
    private Statement loopBody;

    public DoWhileStatement(final Expression condition, final Statement loopBody) {
        super(condition, loopBody);
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
