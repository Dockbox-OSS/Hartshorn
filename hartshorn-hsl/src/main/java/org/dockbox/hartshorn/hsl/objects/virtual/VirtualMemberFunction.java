package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.MemberStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.token.Token;

public class VirtualMemberFunction extends VirtualFunction implements MemberStatement {

    private final Token name;
    private final Token modifier;

    public VirtualMemberFunction(final ParametricExecutableStatement declaration, final VariableScope closure, final boolean isInitializer, final Token name, final Token modifier) {
        super(declaration, closure, isInitializer);
        this.name = name;
        this.modifier = modifier;
    }

    public VirtualMemberFunction(final ParametricExecutableStatement declaration, final VariableScope closure, final InstanceReference instance, final boolean isInitializer, final Token name, final Token modifier) {
        super(declaration, closure, instance, isInitializer);
        this.name = name;
        this.modifier = modifier;
    }

    public boolean hasBody() {
        return this.declaration().statements() != null;
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
