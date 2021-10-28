package org.dockbox.hartshorn.core.context.element;

public interface TypedElementContext<T> extends QualifiedElement {
    TypeContext<T> type();
    String name();
}
