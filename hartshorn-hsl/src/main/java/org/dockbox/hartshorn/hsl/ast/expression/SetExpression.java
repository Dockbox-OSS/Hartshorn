package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class SetExpression extends Expression {

    private final Expression object;
    private final Token name;
    private final Expression value;

    public SetExpression(final Expression object, final Token name, final Expression value) {
        super(name);
        this.object = object;
        this.name = name;
        this.value = value;
    }

    public Expression object() {
        return this.object;
    }

    public Token name() {
        return this.name;
    }

    public Expression value() {
        return this.value;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
