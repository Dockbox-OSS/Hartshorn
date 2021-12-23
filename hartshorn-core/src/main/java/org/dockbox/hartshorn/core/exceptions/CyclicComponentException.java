package org.dockbox.hartshorn.core.exceptions;

import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

public class CyclicComponentException extends ApplicationException{
    public CyclicComponentException(final ConstructorContext<?> constructorContext, final TypeContext<?> other) {
        super("Cyclic dependency detected in constructor: %s. Cyclic dependency between %s and %s".formatted(constructorContext.qualifiedName(), constructorContext.type().qualifiedName(), other == null ? "[Unknown component]" : other.qualifiedName()));
    }
}
