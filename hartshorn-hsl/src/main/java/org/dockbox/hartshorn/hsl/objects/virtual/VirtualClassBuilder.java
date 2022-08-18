package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.ScopeOwner;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.HashMap;
import java.util.Map;

public class VirtualClassBuilder implements ScopeOwner {

    private final Token name;
    private ClassReference superClass;
    private VirtualFunction constructor;
    private VariableScope variableScope;
    private boolean isDynamic;
    private boolean isFinal;

    private final Map<String, VirtualFunction> methods = new HashMap<>();
    private final Map<String, VirtualProperty> fields = new HashMap<>();

    public VirtualClassBuilder(final Token name) {
        this.name = name;
    }

    @Override
    public Token name() {
        return this.name;
    }

    public ClassReference superClass() {
        return this.superClass;
    }

    public VirtualClassBuilder superClass(final ClassReference superClass) {
        this.superClass = superClass;
        return this;
    }

    public VirtualFunction constructor() {
        return this.constructor;
    }

    public VirtualClassBuilder constructor(final VirtualFunction constructor) {
        this.constructor = constructor;
        return this;
    }

    public VariableScope variableScope() {
        return this.variableScope;
    }

    public VirtualClassBuilder variableScope(final VariableScope variableScope) {
        this.variableScope = variableScope;
        return this;
    }

    public boolean isDynamic() {
        return this.isDynamic;
    }

    public VirtualClassBuilder dynamic(final boolean dynamic) {
        this.isDynamic = dynamic;
        return this;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public VirtualClassBuilder isFinal(final boolean finalized) {
        this.isFinal = finalized;
        return this;
    }

    public Map<String, VirtualFunction> methods() {
        return this.methods;
    }

    public Map<String, VirtualProperty> fields() {
        return this.fields;
    }

    public void field(final FieldStatement field) {
        if (this.fields.containsKey(field.name().lexeme())) {
            throw new RuntimeError(field.name(), "Duplicate field '" + this.name.lexeme() + "." + field.name().lexeme() + "'.");
        }
        this.fields.put(field.name().lexeme(), new VirtualProperty(field));
    }

    public void getter(final VirtualMemberFunction getter) {
        if (!getter.declaration().parameters().isEmpty()) {
            throw new RuntimeError(getter.name(), "Getter cannot have parameters.");
        }
        if (this.fields.containsKey(getter.name().lexeme())) {
            this.fields.get(getter.name().lexeme()).getter(getter);
        }
        else throw new RuntimeError(getter.name(), "Could not register getter for unknown property '" + getter.name().lexeme() + "'.");
    }

    public void setter(final VirtualMemberFunction setter) {
        if (setter.hasBody() && setter.declaration().parameters().size() != 1) {
            throw new RuntimeError(setter.name(), "Setter must have exactly one parameter.");
        }
        if (this.fields.containsKey(setter.name().lexeme())) {
            this.fields.get(setter.name().lexeme()).setter(setter);
        }
        else throw new RuntimeError(setter.name(), "Could not register setter for unknown property '" + setter.name().lexeme() + "'.");
    }

    public VirtualClass build() {
        return new VirtualClass(
                this.name.lexeme(),
                this.superClass,
                this.constructor,
                this.variableScope,
                this.methods,
                this.fields,
                this.isFinal,
                this.isDynamic
        );
    }
}
