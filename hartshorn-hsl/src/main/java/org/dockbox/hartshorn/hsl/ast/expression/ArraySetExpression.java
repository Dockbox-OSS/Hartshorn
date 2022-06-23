package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ArraySetExpression extends Expression {

    private final Token name;
    private final Expression index;
    private final Expression value;

    public ArraySetExpression(final Token name, final Expression index, final Expression value) {
        this.name = name;
        this.index = index;
        this.value = value;
    }

    public Token name() {
        return this.name;
    }

    public Expression index() {
        return this.index;
    }

    public Expression value() {
        return this.value;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
