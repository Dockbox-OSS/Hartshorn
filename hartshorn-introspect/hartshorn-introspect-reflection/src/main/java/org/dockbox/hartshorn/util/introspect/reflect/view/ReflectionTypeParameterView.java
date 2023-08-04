package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionTypeParameterView implements TypeParameterView {

    private final Type type;
    private final TypeView<?> declaredBy;

    private Set<TypeParameterView> represents;

    public ReflectionTypeParameterView(final Type type, final TypeView<?> declaredBy) {
        this.type = type;
        this.declaredBy = declaredBy;
    }

    @Override
    public TypeView<?> declaredBy() {
        return this.declaredBy.rawType();
    }

    @Override
    public Set<TypeParameterView> represents() {
        if (this.represents == null) {
            if (this.type instanceof TypeVariable<?> typeVariable) {
                final TypeView<?> superClass = this.declaredBy.genericSuperClass();
                final List<TypeView<?>> genericInterfaces = this.declaredBy.genericInterfaces();

                final Set<TypeView<?>> representationCandidates = new HashSet<>();
                representationCandidates.add(superClass);
                representationCandidates.addAll(genericInterfaces);

                this.represents = representationCandidates.stream()
                        .flatMap(candidate -> this.getRepresentedParameters(typeVariable, candidate).stream())
                        .collect(Collectors.toSet());
            }
        }
        return this.represents;
    }

    private Set<TypeParameterView> getRepresentedParameters(final TypeVariable<?> typeVariable, final TypeView<?> typeView) {
        final Set<TypeParameterView> representedParameters = new HashSet<>();
        for (final TypeParameterView parameterView : typeView.typeParameters().all()) {
            if (parameterView instanceof ReflectionTypeParameterView reflectionTypeParameterView) {
                if (reflectionTypeParameterView.type == typeVariable) {
                    representedParameters.add(reflectionTypeParameterView);
                }
            }
        }
        return representedParameters;
    }

    @Override
    public Option<TypeView<?>> upperBound() {
        return null;
    }

    @Override
    public boolean isBounded() {
        return false;
    }

    @Override
    public boolean isUnbounded() {
        return false;
    }

    @Override
    public boolean isExtends() {
        return false;
    }

    @Override
    public boolean isSuper() {
        return false;
    }

    @Override
    public boolean isClass() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public boolean isRecord() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public String qualifiedName() {
        return this.type.getTypeName();
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {

    }

    @Override
    public String name() {
        return this.type.getTypeName();
    }
}
