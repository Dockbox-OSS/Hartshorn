package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.wildcard.WildcardTypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionTypeParameterView implements TypeParameterView {

    private final Type type;
    private final TypeView<?> declaredBy;
    private final Introspector introspector;

    private Set<TypeParameterView> represents;
    private Set<TypeView<?>> upperBounds;
    private Option<TypeView<?>> resolvedType;

    public ReflectionTypeParameterView(final Type type, final TypeView<?> declaredBy, final Introspector introspector) {
        this.type = type;
        this.declaredBy = declaredBy;
        this.introspector = introspector;
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
            else if (this.type instanceof Class<?>) {
                // A concrete type represents itself, as it is not provided by an input parameter. However, a
                // self-representing type is not a type parameter, so it represents nothing in this context.
                this.represents = Set.of();
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
    public Set<TypeView<?>> upperBounds() {
        if (this.upperBounds == null) {
            if (this.type instanceof TypeVariable<?> typeVariable) {
                this.upperBounds = Arrays.stream(typeVariable.getBounds())
                        .map(this.introspector::introspect)
                        .collect(Collectors.toSet());
            }
            else if (this.type instanceof WildcardType wildcardType) {
                this.upperBounds = Arrays.stream(wildcardType.getUpperBounds())
                        .map(this.introspector::introspect)
                        .collect(Collectors.toSet());
            }
            else {
                this.upperBounds = Set.of();
            }
        }
        return this.upperBounds;
    }

    @Override
    public Option<TypeView<?>> resolvedType() {
        if (this.resolvedType == null) {
            if (this.type instanceof Class<?> clazz) {
                this.resolvedType = Option.of(this.introspector.introspect(clazz));
            }
            else if (this.isWildcard()) {
                this.resolvedType = Option.of(new WildcardTypeView());
            }
            else {
                this.resolvedType = Option.empty();
            }
        }
        return this.resolvedType;
    }

    @Override
    public boolean isBounded() {
        if (this.type instanceof TypeVariable<?> typeVariable) {
            return typeVariable.getBounds().length > 0;
        }
        else if (this.type instanceof WildcardType wildcardType) {
            return wildcardType.getUpperBounds().length > 0;
        }
        return false;
    }

    @Override
    public boolean isUnbounded() {
        return !this.isBounded();
    }

    @Override
    public boolean isClass() {
        return this.type instanceof Class<?>;
    }

    @Override
    public boolean isInterface() {
        return this.type instanceof Class<?> clazz && clazz.isInterface();
    }

    @Override
    public boolean isEnum() {
        return this.type instanceof Class<?> clazz && clazz.isEnum();
    }

    @Override
    public boolean isAnnotation() {
        return this.type instanceof Class<?> clazz && clazz.isAnnotation();
    }

    @Override
    public boolean isRecord() {
        return this.type instanceof Class<?> clazz && clazz.isRecord();
    }

    @Override
    public boolean isVariable() {
        return this.type instanceof TypeVariable<?>;
    }

    @Override
    public boolean isWildcard() {
        return this.type instanceof WildcardType;
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
