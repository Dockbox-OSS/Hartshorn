package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.ScopeOwner;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class VirtualClassBuilder implements ScopeOwner {

    private final Token name;
    private final VariableScope variableScope;

    private VirtualFunction constructor = null;
    private ClassReference superClass = null;
    private boolean isDynamic = false;
    private boolean isFinal = false;

    // LinkedHashMap to preserve declaration order
    private final Map<String, VirtualFunction> methods = new LinkedHashMap<>();
    private final Map<String, VirtualProperty> fields = new LinkedHashMap<>();

    public VirtualClassBuilder(Token name, VariableScope variableScope) {
        this.name = Objects.requireNonNull(name, "Class name cannot be null");
        this.variableScope = Objects.requireNonNull(variableScope, "Variable scope cannot be null");
    }

    @Override
    public Token name() {
        return this.name;
    }

    public VirtualClassBuilder superClass(ClassReference superClass) {
        this.superClass = superClass;
        return this;
    }

    public VirtualClassBuilder constructor(VirtualFunction constructor) {
        this.constructor = constructor;
        return this;
    }

    public VirtualClassBuilder dynamic(boolean dynamic) {
        this.isDynamic = dynamic;
        return this;
    }

    public VirtualClassBuilder isFinal(boolean finalized) {
        this.isFinal = finalized;
        return this;
    }

    public VirtualClassBuilder method(String name, VirtualFunction function) {
        if (this.methods.containsKey(name)) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.DUPLICATE_X_DEFINITION, "method", this.name.lexeme(), name)
                    .at(function.declaration())
                    .build();
        }
        this.methods.put(name, function);
        return this;
    }

    public VirtualClassBuilder methods(Map<String, VirtualFunction> methods) {
        methods.forEach(this::method);
        return this;
    }

    public VirtualClassBuilder field(String name, VirtualProperty property) {
        if (this.fields.containsKey(name)) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.DUPLICATE_X_DEFINITION, "field", this.name.lexeme(), name)
                    .at(property.fieldStatement().name())
                    .build();
        }
        this.fields.put(name, property);
        VirtualFieldMemberFunction getter = property.getter();
        if (getter != null) {
            this.getter(getter);
        }
        VirtualFieldMemberFunction setter = property.setter();
        if (setter != null) {
            this.setter(setter);
        }
        return this;
    }

    public VirtualClassBuilder fields(Map<String, VirtualProperty> fields) {
        fields.forEach(this::field);
        return this;
    }

    private void getter(VirtualFieldMemberFunction getter) {
        if (!getter.declaration().parameters().isEmpty()) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.ILLEGAL_GETTER_WITH_PARAMETERS, getter.name().lexeme())
                    .at(getter.name())
                    .build();
        }
        if (this.fields.containsKey(getter.name().lexeme())) {
            this.fields.get(getter.name().lexeme()).getter(getter);
            return;
        }
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.UNDEFINED_PROPERTY_ACCESSOR, "getter", getter.name().lexeme())
                .at(getter.name())
                .build();
    }

    private void setter(VirtualFieldMemberFunction setter) {
        if (setter.hasBody() && setter.declaration().parameters().size() != 1) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.ILLEGAL_SETTER_PARAMETER_MISMATCH,
                            setter.name().lexeme(),
                            setter.declaration().parameters().size()
                    ).at(setter.name())
                    .build();
        }
        if (this.fields.containsKey(setter.name().lexeme())) {
            this.fields.get(setter.name().lexeme()).setter(setter);
            return;
        }
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.UNDEFINED_PROPERTY_ACCESSOR, "setter", setter.name().lexeme())
                .at(setter.name())
                .build();
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
