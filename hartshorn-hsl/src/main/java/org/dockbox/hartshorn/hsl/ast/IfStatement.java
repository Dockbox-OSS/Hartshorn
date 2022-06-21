package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class IfStatement extends Statement {

    private final Expression condition;
    private final List<Statement> thenBranch;
    private final List<Statement> elseBranch;

    public IfStatement(final Expression condition, final List<Statement> thenBranch, final List<Statement> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public List<Statement> getThenBranch() {
        return this.thenBranch;
    }

    public List<Statement> getElseBranch() {
        return this.elseBranch;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
