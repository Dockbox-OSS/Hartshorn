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

package org.dockbox.hartshorn.api.keys;

import org.dockbox.hartshorn.api.CheckedFunction;
import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.MetaProvider;
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.util.Reflect;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Keys {

    private Keys() {}

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
        return Keys.persistent(type, name, Hartshorn.context().get(MetaProvider.class).lookup(owningClass));
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

        return new TypedPersistentDataKey<>(name, Keys.id(name, owner), owner, type);
    }

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
    public static String id(String name, TypedOwner owner) {
        name = name.toLowerCase(Locale.ROOT).replaceAll("[ .]", "").replaceAll("-", "_");
        return owner.id() + ':' + name;
    }

    public static <K, A> KeyBuilder<K, A> builder(Class<K> target, Class<A> type) {
        return new KeyBuilder<>();
    }

    @SuppressWarnings("ClassReferencesSubclass")
    public static class KeyBuilder<K, A> {

        protected BiFunction<K, A, TransactionResult> setter;
        protected Function<K, Exceptional<A>> getter;

        private KeyBuilder() {
            this.withoutGetter();
            this.withoutSetter();
        }

        public KeyBuilder<K, A> withoutSetter() {
            this.setter = (k, a) -> {
                throw new UnsupportedOperationException("cannot set the value of this key");
            };
            return this;
        }

        public KeyBuilder<K, A> withoutGetter() {
            this.getter = k -> Exceptional.empty();
            return this;
        }

        public KeyBuilder<K, A> withSetter(BiConsumer<K, A> setter) {
            this.setter = (k, a) -> {
                setter.accept(k, a);
                return TransactionResult.success();
            };
            return this;
        }

        public KeyBuilder<K, A> withSetterResult(BiFunction<K, A, TransactionResult> setter) {
            this.setter = setter;
            return this;
        }

        public KeyBuilder<K, A> withGetterSafe(Function<K, Exceptional<A>> getter) {
            this.getter = getter;
            return this;
        }

        public KeyBuilder<K, A> withGetter(CheckedFunction<K, A> getter) {
            this.getter = k -> Exceptional.of(() -> getter.apply(k));
            return this;
        }

        public RemovableKeyBuilder<K, A> withRemover(Consumer<K> remover) {
            return new RemovableKeyBuilder<K, A>()
                    .withSetterResult(this.setter)
                    .withGetterSafe(this.getter)
                    .withRemover(remover);
        }

        public Key<K, A> build() {
            return new Key<>(this.setter, this.getter) {
            };
        }
    }

    public static class RemovableKeyBuilder<K, A> extends KeyBuilder<K, A> {

        private Consumer<K> remover;

        private RemovableKeyBuilder() {
        }

        @Override
        public RemovableKeyBuilder<K, A> withRemover(Consumer<K> remover) {
            this.remover = remover;
            return this;
        }

        @Override
        public RemovableKey<K, A> build() {
            if (this.remover == null) this.remover = k -> {};
            return new RemovableKey<>(this.setter, this.getter, this.remover) {
            };
        }
    }
}
