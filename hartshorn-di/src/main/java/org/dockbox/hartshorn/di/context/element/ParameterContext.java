package org.dockbox.hartshorn.di.context.element;

import java.lang.reflect.Parameter;

import lombok.Getter;

@SuppressWarnings("unchecked")
public class ParameterContext<T> extends AnnotatedElementContext<Parameter> {

    private final Parameter parameter;
    @Getter private final boolean isVarargs;

    private String name;
    private TypeContext<T> type;

    private ParameterContext(final Parameter parameter) {
        this.parameter = parameter;
        this.isVarargs = parameter.isVarArgs();
    }

    public static <T> ParameterContext<T> of(final Parameter parameter) {
        return new ParameterContext<>(parameter);
    }

    public String name() {
        if (this.name == null) {
            this.name = this.element().getName();
        }
        return this.name;
    }

    public TypeContext<T> type() {
        if (this.type == null) {
            this.type = TypeContext.of((Class<T>) this.element().getType());
        }
        return this.type;
    }

    @Override
    protected Parameter element() {
        return this.parameter;
    }
}
