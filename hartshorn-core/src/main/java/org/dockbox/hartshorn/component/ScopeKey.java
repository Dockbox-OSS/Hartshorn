package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.util.introspect.ParameterizableType;

/**
 * A key that can be used to identify a {@link Scope}. This contains the required metadata to
 * identify a scope, and can be used to manage scoped components in a {@link ScopeAwareComponentProvider}.
 *
 * <p>Scope keys contain a {@link ParameterizableType} that describes the type of the scope. This type can
 * be parameterized. Therefore, key instances differentiate between e.g. {@code TypeScope<String>} and
 * {@code TypeScope<Integer>}.
 *
 * <p>Keys are always immutable. Specific implementations may choose to provide utility builders or factories,
 * but should never allow the final {@link ScopeKey} to be mutable.
 *
 * @see ScopeAwareComponentProvider
 * @see Scope
 *
 * @param <T> the type of the scope
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ScopeKey<T extends Scope> {

    /**
     * Returns the name of the scope type. This is the simple name of the type, without any package
     * information, but including any type parameters.
     *
     * @return the name of the scope type
     */
    String name();

    /**
     * Returns the type of the scope. This contains the full type information, including any type
     * parameters.
     *
     * @return the type of the scope
     */
    ParameterizableType scopeType();
}
