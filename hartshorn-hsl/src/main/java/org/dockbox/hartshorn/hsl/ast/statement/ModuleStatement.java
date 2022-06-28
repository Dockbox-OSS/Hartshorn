package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ModuleStatement extends Statement {

    private final Token name;

    public ModuleStatement(final Token name) {
        super(name);
        this.name = name;
    }

    public Token name() {
        return this.name;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
