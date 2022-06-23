package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ElvisExpression extends Expression {

    private final Expression condition;
    private final Token elvisOpe;
    private final Expression rightExp;

    public ElvisExpression(final Expression condition,
                           final Token elvisOpe,
                           final Expression rightExp) {
        this.condition = condition;
        this.elvisOpe = elvisOpe;
        this.rightExp = rightExp;
    }

    public Expression condition() {
        return this.condition;
    }

    public Token elvisOperator() {
        return this.elvisOpe;
    }

    public Expression rightExpression() {
        return this.rightExp;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
