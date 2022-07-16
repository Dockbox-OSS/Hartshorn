package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ForStatement extends BodyStatement {

    private final VariableStatement initializer;
    private final Expression condition;
    private final Statement increment;

    public ForStatement(final VariableStatement initializer, final Expression condition, final Statement increment, final BlockStatement loopBody) {
        super(initializer.line(), loopBody);
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
    }

    public VariableStatement initializer() {
        return this.initializer;
    }

    public Expression condition() {
        return this.condition;
    }

    public Statement increment() {
        return this.increment;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
