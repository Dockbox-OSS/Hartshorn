package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class IfStatement extends Statement {

    private final Expression condition;
    private final List<Statement> thenBranch;
    private final List<Statement> elseBranch;

    public IfStatement(final Expression condition, final List<Statement> thenBranch, final List<Statement> elseBranch) {
        super(condition.line());
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expression condition() {
        return this.condition;
    }

    public List<Statement> thenBranch() {
        return this.thenBranch;
    }

    public List<Statement> elseBranch() {
        return this.elseBranch;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
