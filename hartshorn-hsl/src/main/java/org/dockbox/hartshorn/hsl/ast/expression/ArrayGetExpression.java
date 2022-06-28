package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ArrayGetExpression extends Expression {

    private final Expression size;

    public ArrayGetExpression(final Expression size) {
        super(size.line());
        this.size = size;
    }

    public Expression size() {
        return this.size;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
