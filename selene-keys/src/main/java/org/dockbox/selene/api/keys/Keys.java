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

import org.dockbox.selene.api.CheckedFunction;
import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.util.Reflect;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Keys {

    private Keys() {}

    /**
     * Convert the given name to a valid ID, merging its owner ID and the name in the following format:
     * <pre>{@code
     * Sample Item -> $ownerId:sample_item
     * }</pre>
     *
     * @param name
     *         the name
     * @param owner
     *         the owner
     *
     * @return the string
     */
    public static String convertId(String name, TypedOwner owner) {
        name = name.toLowerCase(Locale.ROOT).replaceAll("[ .]", "").replaceAll("-", "_");
        return owner.id() + ':' + name;
    }

    /**
     * Persistent key of persistent data key.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param name
     *         the name
     * @param owningClass
     *         the owning class
     *
     * @return the persistent data key
     */
    public static <T> PersistentDataKey<T> persistent(Class<T> type, String name, Class<?> owningClass) {
        return Keys.persistent(type, name, Selene.context().get(OwnerLookup.class).lookup(owningClass));
    }

    /**
     * Persistent key of persistent data key.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param name
     *         the name
     * @param owner
     *         the owner
     *
     * @return the persistent data key
     */
    public static <T> PersistentDataKey<T> persistent(Class<T> type, String name, TypedOwner owner) {
        if (!Reflect.isNative(type))
            throw new RuntimeException("Unsupported data type for persistent key: " + type.getCanonicalName());

        return new TypedPersistentDataKey<>(name, Keys.convertId(name, owner), owner, type);
    }

    /**
     * Checked dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> removable(BiFunction<K, A, TransactionResult> setter, Function<K, Exceptional<A>> getter) {
        return new Key<K, A>(setter, getter) {
        };
    }

    /**
     * Unsafe dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> wrapRemovable(BiConsumer<K, A> setter, CheckedFunction<K, A> getter) {
        return new Key<K, A>(
                Keys.wrapSetter(setter), k -> Exceptional.of(() -> getter.apply(k))) {
        };
    }

    /**
     * Transact setter bi function.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     *
     * @return the bi function
     */
    public static <K, A> BiFunction<K, A, TransactionResult> wrapSetter(BiConsumer<K, A> setter) {
        return Keys.wrapSetter(setter, TransactionResult.success());
    }

    /**
     * Transact setter bi function.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param defaultResult
     *         the default result
     *
     * @return the bi function
     */
    public static <K, A> BiFunction<K, A, TransactionResult> wrapSetter(BiConsumer<K, A> setter, TransactionResult defaultResult) {
        return (t, u) -> {
            setter.accept(t, u);
            return defaultResult;
        };
    }

    /**
     * Unsafe checked dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> wrapGetter(BiFunction<K, A, TransactionResult> setter, CheckedFunction<K, A> getter) {
        return new Key<K, A>(setter, k -> Exceptional.of(() -> getter.apply(k))) {
        };
    }

    /**
     * Dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> of(BiConsumer<K, A> setter, Function<K, Exceptional<A>> getter, Consumer<K> remover) {
        return new RemovableKey<K, A>(Keys.wrapSetter(setter), getter, remover) {
        };
    }

    /**
     * Checked dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> removable(
            BiFunction<K, A, TransactionResult> setter,
            Function<K, Exceptional<A>> getter,
            Consumer<K> remover
    ) {
        return new RemovableKey<K, A>(setter, getter, remover) {
        };
    }

    /**
     * Unsafe dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> wrapRemovable(BiConsumer<K, A> setter, CheckedFunction<K, A> getter, Consumer<K> remover) {
        return new RemovableKey<K, A>(
                Keys.wrapSetter(setter), k -> Exceptional.of(() -> getter.apply(k)), remover) {
        };
    }

    public static <K, A> Key<K, A> ofGetter(Function<K, Exceptional<A>> getter) {
        return of((k, a) -> {
            throw new UnsupportedOperationException("Cannot set the value of this key");
        }, getter);
    }

    /**
     * Dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> of(BiConsumer<K, A> setter, Function<K, Exceptional<A>> getter) {
        return new Key<K, A>(Keys.wrapSetter(setter), getter) {
        };
    }

    public static <K, A> Key<K, A> ofSetter(BiConsumer<K, A> setter) {
        return of(setter, in -> Exceptional.none());
    }
}
