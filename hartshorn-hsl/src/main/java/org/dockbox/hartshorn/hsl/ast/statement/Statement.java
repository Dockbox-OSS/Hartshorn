package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public abstract class Statement {
    public abstract <R> R accept(StatementVisitor<R> visitor);
}
