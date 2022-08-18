package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class FieldSetStatement extends FieldMemberStatement {

    private final Parameter parameter;

    public FieldSetStatement(final Token modifier, final Token set, final FieldStatement fieldStatement, final List<Statement> body, final Parameter parameter) {
        super(modifier, set, fieldStatement, parameter == null ? List.of() : List.of(parameter), body);
        this.parameter = parameter;
    }

    public Parameter parameter() {
        return this.parameter;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
