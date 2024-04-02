package org.dockbox.hartshorn.context;

import java.util.List;

import org.dockbox.hartshorn.util.option.Option;

/**
 * Immutable view of a {@link Context}, providing read-only access to the contexts stored within.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ContextView {

    /**
     * Returns all contexts stored in the current context.
     * @return All contexts stored in the current context.
     */
    List<ContextView> contexts();

    /**
     * Returns the first context of the given type.
     *
     * @param context The type of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type.
     */
    <C extends ContextView> Option<C> firstContext(Class<C> context);

    /**
     * Returns all contexts of the given type. If no contexts of the given type are found, an empty list is returned.
     *
     * @param context The type of the context.
     * @return All contexts of the given type.
     * @param <C> The type of the context.
     */
    <C extends ContextView> List<C> contexts(Class<C> context);

    /**
     * Returns the first context matching the given identity. If no context is found, an attempt may be made to create
     * a new context using the fallback function of the identity. If no fallback function is present, or it is not
     * compatible with the current context, an empty option is returned.
     *
     * @param key The identity of the context.
     * @return The first context matching the given identity.
     * @param <C> The type of the context.
     */
    <C extends ContextView> Option<C> firstContext(ContextIdentity<C> key);

    /**
     * Returns all contexts matching the given identity. If no contexts are found, an empty list is returned.
     *
     * @param key The identity of the context.
     * @return All contexts matching the given identity.
     * @param <C> The type of the context.
     */
    <C extends ContextView> List<C> contexts(ContextIdentity<C> key);

    /**
     * Copies all child contexts from the current context to the given context. This will not copy the current context
     * itself.
     *
     * @param context The context to copy to.
     */
    void copyContextTo(Context context);
}
