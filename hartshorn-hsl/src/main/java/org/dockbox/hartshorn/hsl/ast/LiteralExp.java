package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class LiteralExp extends Expression {

    private final Object value;

    public LiteralExp(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
