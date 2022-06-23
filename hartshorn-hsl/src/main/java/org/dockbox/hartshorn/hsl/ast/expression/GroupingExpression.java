package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class GroupingExpression extends Expression {

    private final Expression expression;

    public GroupingExpression(final Expression expression) {
        this.expression = expression;
    }

    public Expression expression() {
        return this.expression;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
