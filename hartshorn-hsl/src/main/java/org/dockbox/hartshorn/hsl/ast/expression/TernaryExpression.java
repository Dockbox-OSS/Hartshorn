package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class TernaryExpression extends Expression {

    private final Expression condition;
    private final Token ternaryOp;
    private final Expression firstExp;
    private final Token colon;
    private final Expression secondExp;

    public TernaryExpression(final Expression condition, final Token ternaryOp,
                             final Expression firstExp, final Token colon,
                             final Expression secondExp) {
        super(condition.line());
        this.condition = condition;
        this.ternaryOp = ternaryOp;
        this.firstExp = firstExp;
        this.colon = colon;
        this.secondExp = secondExp;
    }

    public Expression condition() {
        return this.condition;
    }

    public Token ternaryOp() {
        return this.ternaryOp;
    }

    public Expression firstExpression() {
        return this.firstExp;
    }

    public Token colon() {
        return this.colon;
    }

    public Expression secondExpression() {
        return this.secondExp;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
