package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class BitwiseExp extends Expression {

    private final Expression leftExp;
    private final Token operator;
    private final Expression rightExp;

    public BitwiseExp(final Expression leftExp, final Token operator, final Expression rightExp) {
        this.leftExp = leftExp;
        this.operator = operator;
        this.rightExp = rightExp;
    }

    public Expression getLeftExp() {
        return this.leftExp;
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
