package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.token.Token;

public class VirtualProperty {

    private final FieldStatement fieldStatement;

    private VirtualFieldMemberFunction getter;
    private VirtualFieldMemberFunction setter;

    private Token readModifier;
    private Token writeModifier;

    public VirtualProperty(FieldStatement fieldStatement) {
        this.fieldStatement = fieldStatement;
        this.readModifier = fieldStatement.modifier();
        this.writeModifier = fieldStatement.modifier();
    }

    public FieldStatement fieldStatement() {
        return this.fieldStatement;
    }

    public VirtualFieldMemberFunction getter() {
        return this.getter;
    }

    public VirtualProperty getter(VirtualFieldMemberFunction getter) {
        this.getter = getter;
        if (getter.modifier() != null) {
            this.readModifier = getter.modifier();
        }
        return this;
    }

    public VirtualFieldMemberFunction setter() {
        return this.setter;
    }

    public VirtualProperty setter(VirtualFieldMemberFunction setter) {
        this.setter = setter;
        if (setter.modifier() != null) {
            this.writeModifier = setter.modifier();
        }
        return this;
    }

    public Token readModifier() {
        return this.readModifier;
    }

    public VirtualProperty readModifier(Token readModifier) {
        this.readModifier = readModifier;
        return this;
    }

    public Token writeModifier() {
        return this.writeModifier;
    }

    public VirtualProperty writeModifier(Token writeModifier) {
        this.writeModifier = writeModifier;
        return this;
    }
}
