package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public abstract non-sealed class CustomStatement<T extends CustomStatement<T>> extends Statement implements CustomASTNode<T, Void> {

    private final StatementModule<T> module;

    protected CustomStatement(ASTNode at, StatementModule<T> module) {
        super(at);
        this.module = module;
    }

    @Override
    public StatementModule<T> module() {
        return this.module;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        throw new UnsupportedOperationException();
    }
}
