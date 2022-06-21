package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class TernaryExp extends Expression {

    private final Expression condition;
    private final Token ternaryOp;
    private final Expression firstExp;
    private final Token colon;
    private final Expression secondExp;

    public TernaryExp(final Expression condition, final Token ternaryOp,
                      final Expression firstExp, final Token colon,
                      final Expression secondExp) {
        this.condition = condition;
        this.ternaryOp = ternaryOp;
        this.firstExp = firstExp;
        this.colon = colon;
        this.secondExp = secondExp;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public Token getTernaryOp() {
        return this.ternaryOp;
    }

    public Expression getFirstExp() {
        return this.firstExp;
    }

    public Token getColon() {
        return this.colon;
    }

    public Expression getSecondExp() {
        return this.secondExp;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
