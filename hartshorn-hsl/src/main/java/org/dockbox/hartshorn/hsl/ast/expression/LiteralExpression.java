package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class LiteralExpression extends Expression {

    private final Object value;

    public LiteralExpression(final Token at, final Object value) {
        super(at);
        this.value = value;
    }

    public Object value() {
        return this.value;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
