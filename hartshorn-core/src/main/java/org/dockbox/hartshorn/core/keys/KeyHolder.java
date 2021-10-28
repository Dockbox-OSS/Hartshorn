/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.keys;

import org.dockbox.hartshorn.core.context.ContextCarrier;
import org.dockbox.hartshorn.core.domain.Exceptional;

/**
 * Low-level interface which can be used to access and retrieve values from an implementation of this
 * interface using the {@link Key} type. This interface provides default implementations, expecting
 * the implementation is the type to be modified. For more complex implementations these methods can
 * be overridden.
 *
 * @param <T>
 *         The type which the {@link Key} can modify.
 */
@SuppressWarnings("rawtypes")
public interface KeyHolder<T extends KeyHolder> extends ContextCarrier {

    /**
     * Apply a given value of type {@code A} using a given {@link Key} type to the implementation of
     * this interface. The type parameter of the value to apply, constrained by the type parameter of
     * the given {@link Key}.
     *
     * @param <A>
     *         The type parameter of the applied value.
     * @param key
     *         The key to apply, providing the constraints for the type to apply to and the type of
     *         the applied value.
     * @param appliedValue
     *         The applied value.
     *
     * @return The transaction result. If the transaction failed the {@link TransactionResult} will
     *         provide a {@link TransactionResult#message() message}.
     */
    default <A> TransactionResult set(final Key<T, A> key, final A appliedValue) {
        try {
            return key.set((T) this, appliedValue);
        }
        catch (final ClassCastException e) {
            final String message = "Attempted to apply %s to non-supporting type %s".formatted(key, this);
            return TransactionResult.fail(message);
        }
    }

    /**
     * Retrieves a value from the implementation of this interface using a given {@link Key}. The
     * {@link Key} provides the constraints for the returned type using type parameters {@code A}.
     *
     * @param <A>
     *         The type parameter of the returned value, constrained by the type parameter of the
     *         given {@link Key}.
     * @param key
     *         The key to use when retrieving a value, providing the constraints for the returned
     *         value.
     *
     * @return The value wrapped in a {@link Exceptional}, which will contain a {@link
     *         ClassCastException} if <em>this</em> does not match the constraint of the given {@link
     *         Key}.
     */
    default <A, K extends T> Exceptional<A> get(final Key<K, A> key) {
        return key.get((K) this);
    }

    default <A> void remove(final RemovableKey<T, A> key) {
        key.remove((T) this);
    }
}
