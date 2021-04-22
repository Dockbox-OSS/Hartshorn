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

package org.dockbox.selene.api.keys;

import org.dockbox.selene.api.domain.Exceptional;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A low-level type which can be used in combination with a {@link KeyHolder} to dynamically apply,
 * retrieve, and remove values from types. The apply and retrieve functionality is inherited and
 * unchanged from {@link Key}.
 *
 * @param <K>
 *         The type parameter indicating the constraint for the type to apply to/retrieve from.
 * @param <A>
 *         The type parameter indicating the constraint for the value to be applied/retrieved.
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class RemovableKey<K, A> extends Key<K, A> {

    private final Consumer<K> remover;

    /**
     * Instantiates a new Key using a given setter and getter.
     *
     * @param setter
     *         The setter, accepting two values. The first being the type to apply to,
     *         constrained using type parameter {@code K}. The second being the value to apply,
     *         constrained using type parameter {@code A}.
     * @param getter
     *         The getter, accepting one value, and returning another. The accepting value being
     *         the type to retrieve from, constrained using type parameter {@code K}. The return value
     *         being the value retreived from the type, constrained using type parameter {@code A}.
     * @param remover
     *         The remover, accepting one value. The accepting value being the value to remove
     *         from, constrained using the type parameter {@code K}.
     */
    protected RemovableKey(
            BiFunction<K, A, TransactionResult> setter,
            Function<K, Exceptional<A>> getter,
            Consumer<K> remover
    ) {
        super(setter, getter);
        this.remover = remover;
    }

    /**
     * Removes a value from the given type constrained by type parameter {@code K}.
     *
     * @param keyType
     *         The data holder, constrained by type parameter {@code K}.
     */
    public void remove(K keyType) {
        this.remover.accept(keyType);
    }
}
