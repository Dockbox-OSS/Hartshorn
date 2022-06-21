package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ArrayGetExp extends Expression {

    private final Expression size;

    public ArrayGetExp(final Expression size) {
        this.size = size;
    }

    public Expression getSize() {
        return this.size;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
