package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class WhileStatement extends Statement {

    private final Expression condition;
    private final Statement loopBody;

    public WhileStatement(final Expression condition, final Statement loopBody) {
        super(condition.line());
        this.condition = condition;
        this.loopBody = loopBody;
    }

    public Expression condition() {
        return this.condition;
    }

    public Statement loopBody() {
        return this.loopBody;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
