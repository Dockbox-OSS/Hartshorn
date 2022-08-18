package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.token.Token;

public class VirtualProperty {

    private final FieldStatement fieldStatement;

    private VirtualMemberFunction getter;
    private VirtualMemberFunction setter;

    private Token readModifier;
    private Token writeModifier;

    public VirtualProperty(final FieldStatement fieldStatement) {
        this.fieldStatement = fieldStatement;
        this.readModifier = fieldStatement.modifier();
        this.writeModifier = fieldStatement.modifier();
    }

    public FieldStatement fieldStatement() {
        return this.fieldStatement;
    }

    public VirtualMemberFunction getter() {
        return this.getter;
    }

    public VirtualProperty getter(final VirtualMemberFunction getter) {
        this.getter = getter;
        if (getter.modifier() != null) this.readModifier = getter.modifier();
        return this;
    }

    public VirtualMemberFunction setter() {
        return this.setter;
    }

    public VirtualProperty setter(final VirtualMemberFunction setter) {
        this.setter = setter;
        if (setter.modifier() != null) this.writeModifier = setter.modifier();
        return this;
    }

    public Token readModifier() {
        return this.readModifier;
    }

    public VirtualProperty readModifier(final Token readModifier) {
        this.readModifier = readModifier;
        return this;
    }

    public Token writeModifier() {
        return this.writeModifier;
    }

    public VirtualProperty writeModifier(final Token writeModifier) {
        this.writeModifier = writeModifier;
        return this;
    }
}
