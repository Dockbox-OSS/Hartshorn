package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class UnaryExp extends Expression {

    private final Token operator;
    private final Expression rightExp;

    public UnaryExp(final Token operator, final Expression rightExp) {
        this.operator = operator;
        this.rightExp = rightExp;
    }

    public Token getOperator() {
        return this.operator;
    }

    public Expression getRightExp() {
        return this.rightExp;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
