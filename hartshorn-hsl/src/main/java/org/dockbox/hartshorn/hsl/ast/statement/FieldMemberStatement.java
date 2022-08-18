package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;

public abstract class FieldMemberStatement extends ParametricExecutableListStatement implements MemberStatement {

    private final Token modifier;
    private final Token keyword;
    private final FieldStatement fieldStatement;

    protected FieldMemberStatement(final Token modifier, final Token keyword, final FieldStatement fieldStatement, final List<Parameter> parameters, final List<Statement> body) {
        super(modifier != null ? modifier : keyword, parameters, body);
        this.modifier = modifier;
        this.keyword = keyword;
        this.fieldStatement = fieldStatement;
    }

    public boolean hasBody() {
        return this.statements() != null;
    }

    public Token keyword() {
        return this.keyword;
    }

    public FieldStatement field() {
        return this.fieldStatement;
    }

    @Override
    public Token name() {
        return this.fieldStatement.name();
    }

    @Override
    public Token modifier() {
        return this.modifier;
    }
}
