package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FieldMemberStatement;
import org.dockbox.hartshorn.hsl.ast.statement.MemberStatement;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.token.Token;

public class VirtualFieldMemberFunction extends VirtualFunction implements MemberStatement {

    private final Token name;
    private final Token modifier;

    public VirtualFieldMemberFunction(FieldMemberStatement declaration, VariableScope closure) {
        super(declaration, closure, false);
        this.name = declaration.name();
        this.modifier = declaration.modifier();
    }

    public boolean hasBody() {
        return this.declaration().body() != null;
    }

    @Override
    public FieldMemberStatement declaration() {
        return (FieldMemberStatement) super.declaration();
    }

    @Override
    public Token name() {
        return this.name;
    }

    @Override
    public Token modifier() {
        return this.modifier;
    }
}
