package org.dockbox.hartshorn.inject;

/**
 * A {@link Provider} that is not aware of the type it provides. This is useful when the type
 * is not known at compile time, but only at runtime (e.g. in suppliers).
 *
 * @param <T> The type instance to provide.
 */
@FunctionalInterface
public non-sealed interface NonTypeAwareProvider<T> extends Provider<T> {
}
