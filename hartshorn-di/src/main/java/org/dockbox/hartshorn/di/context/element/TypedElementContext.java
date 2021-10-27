package org.dockbox.hartshorn.di.context.element;

public interface TypedElementContext<T> extends QualifiedElement {
    TypeContext<T> type();
    String name();
}
