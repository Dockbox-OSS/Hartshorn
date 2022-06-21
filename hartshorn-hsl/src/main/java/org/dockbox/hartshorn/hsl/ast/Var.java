package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class Var extends Statement {

    private final Token name;
    private final Expression initializer;

    public Var(final Token name, final Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public Token getName() {
        return this.name;
    }

    public Expression getInitializer() {
        return this.initializer;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
