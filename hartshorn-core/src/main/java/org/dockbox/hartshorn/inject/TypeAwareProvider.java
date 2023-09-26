package org.dockbox.hartshorn.inject;

public non-sealed interface TypeAwareProvider<T> extends Provider<T> {
    Class<? extends T> type();
}
