package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public abstract non-sealed class CustomExpression<T extends CustomExpression<T>> extends Expression implements CustomASTNode<T, Object> {

    private final ExpressionModule<T> module;

    protected CustomExpression(ASTNode at, ExpressionModule<T> module) {
        super(at);
        this.module = module;
    }

    @Override
    public ExpressionModule<T> module() {
        return this.module;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        throw new UnsupportedOperationException();
    }
}
