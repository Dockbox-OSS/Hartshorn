package org.dockbox.hartshorn.util.introspect.view;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public interface TypeParameterView extends View {

    TypeView<?> declaredBy();

    Set<TypeParameterView> represents();

    Set<TypeView<?>> upperBounds();

    Option<TypeView<?>> resolvedType();

    boolean isBounded();

    boolean isUnbounded();

    boolean isClass();

    boolean isInterface();

    boolean isEnum();

    boolean isAnnotation();

    boolean isRecord();

    boolean isVariable();

    boolean isWildcard();
}
