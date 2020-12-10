/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.objects.keys;

import org.dockbox.selene.core.objects.Exceptional;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 A low-level type which can be used in combination with a {@link KeyHolder} to dynamically apply and retrieve
 values from types.

 @param <K>
 The type parameter indicating the constraint for the type to apply to/retrieve from.
 @param <A>
 The type parameter indicating the constraint for the value to be applied/retrieved.
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class Key<K, A> {

    private final BiFunction<K, A, TransactionResult> setter;
    private final Function<K, Exceptional<A>> getter;
    private final Consumer<K> remover;

    /**
     Instantiates a new Key using a given setter and getter.

     @param setter
     The setter, accepting two values. The first being the type to apply to, constrained using type parameter {@link K}.
     The second being the value to apply, constrained using type parameter {@link A}.
     @param getter
     The getter, accepting one value, and returning another. The accepting value being the type to retrieve from,
     constrained using type parameter {@link K}. The return value being the value retreived from the type, constrained
     using type parameter {@link A}.
     */
    protected Key(BiFunction<K, A, TransactionResult> setter, Function<K, Exceptional<A>> getter) {
        this(setter, getter, k -> {});
    }

    protected Key(BiFunction<K, A, TransactionResult> setter, Function<K, Exceptional<A>> getter, Consumer<K> remover) {
        this.setter = setter;
        this.getter = getter;
        this.remover = remover;
    }

    /**
     Apply a given value of type parameter {@link A} to a given type constrained by type parameter {@link K}.

     @param keyType
     The data holder, constrained by type parameter {@link K}.
     @param appliedValue
     The value to apply, constrained by type parameter {@link A}.
     */
    public TransactionResult set(K keyType, A appliedValue) {
        return this.setter.apply(keyType, appliedValue);
    }

    /**
     Retrieves a value from the given type constrained by type parameter {@link K}.

     @param keyType
     The data holder, constrained by type parameter {@link K}.

     @return The retrieved value, constrained by type parameter {@link A}.
     */
    public Exceptional<A> get(K keyType) {
        return this.getter.apply(keyType);
    }

    public void remove(K keyType) {
        this.remover.accept(keyType);
    }

    /**
     Resolves the correct parent type for a given key, so it can be applied safely. This is useful when applying
     keys to supertypes which extend from multiple Keyholders, like {@link org.dockbox.selene.core.objects.player.Player}.
     This method applies a constraint on the type, so that the supertype has to extend type parameter {@link K}, so
     we can ensure no {@link ClassCastException} will be thrown. This also prevents us from applying a {@link Key} made
     for e.g. {@link org.dockbox.selene.core.objects.item.Item} to a {@link org.dockbox.selene.core.objects.player.Player}.

     @param <T>
     The type parameter indicating the supertype to resolve

     @return The current instance, resolved for the supertype.
     */
    @SuppressWarnings("unchecked")
    public <T extends K> Key<T, A> resolve() {
        return (Key<T, A>) this;
    }

}
