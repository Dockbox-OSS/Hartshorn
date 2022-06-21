package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class InfixExpression extends Expression {

    private final Expression leftExp;
    private final Token infixOperator;
    private final Expression rightExp;

    public InfixExpression(final Expression leftExp, final Token infixOperator, final Expression rightExp) {
        this.leftExp = leftExp;
        this.infixOperator = infixOperator;
        this.rightExp = rightExp;
    }

    public Expression getLeftExp() {
        return this.leftExp;
    }

    public Token getInfixOperatorName() {
        return this.infixOperator;
    }

    public Expression getRightExp() {
        return this.rightExp;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
