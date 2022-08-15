package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class FieldStatement extends FinalizableStatement implements MemberStatement, NamedNode {

    private final Token modifier;
    private final Token name;
    private final Expression initializer;

    public FieldStatement(final Token modifier, final Token name, final Expression initializer, final boolean isFinal) {
        super(modifier != null ? modifier : name, isFinal);
        this.modifier = modifier;
        this.name = name;
        this.initializer = initializer;
    }

    public Expression initializer() {
        return this.initializer;
    }

    @Override
    public Token name() {
        return this.name;
    }

    @Override
    public Token modifier() {
        return this.modifier;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
