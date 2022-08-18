package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class FieldGetStatement extends FieldMemberStatement {

    public FieldGetStatement(final Token modifier, final Token get, final FieldStatement fieldStatement, final List<Statement> body) {
        super(modifier, get, fieldStatement, List.of(), body);
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
