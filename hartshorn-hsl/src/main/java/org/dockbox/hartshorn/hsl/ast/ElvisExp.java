package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ElvisExp extends Expression {

    private final Expression condition;
    private final Token elvisOpe;
    private final Expression rightExp;

    public ElvisExp(final Expression condition,
                    final Token elvisOpe,
                    final Expression rightExp) {
        this.condition = condition;
        this.elvisOpe = elvisOpe;
        this.rightExp = rightExp;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public Token getElvisOpe() {
        return this.elvisOpe;
    }

    public Expression getRightExp() {
        return this.rightExp;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
