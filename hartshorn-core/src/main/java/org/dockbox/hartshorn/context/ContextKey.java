/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.context;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A {@link ContextKey} is a key which can be used to retrieve a context value from a {@link ContextView
 * context} instance. The key is used to identify the value, and can be used to create a new value if none
 * exists.
 *
 * <p>Context keys do not have to be unique, but it is recommended to re-use the same unique key to
 * retrieve the same value. This is not enforced, but can be useful to avoid unexpected behavior when
 * using default values.
 *
 * @param <T> The type of the value.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class ContextKey<T extends ContextView> implements ContextIdentity<T> {

    private final Class<T> type;
    private final String name;
    private final Function<ApplicationContext, T> fallback;
    private final boolean requiresApplicationContext;

    private ContextKey(Builder<T> builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.fallback = builder.fallback;
        this.requiresApplicationContext = builder.requiresApplicationContext;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean requiresApplicationContext() {
        return this.fallback == null || this.requiresApplicationContext;
    }

    @Override
    public T create() {
        if (this.requiresApplicationContext()) {
            throw new IllegalStateException("Cannot create context value without an application context");
        }
        return this.create(null);
    }

    /**
     * Creates a new context value for this key. If a fallback function is present, it will be used
     * to create the value. If no fallback function is present, a new instance of the context type
     * will be created using the provided application context.
     *
     * @param context The application context to use to create the context value.
     * @return The newly created context value.
     */
    public T create(ApplicationContext context) {
        return this.create(context, context);
    }

    /**
     * Creates a new context value for this key. If a fallback function is present, it will be used
     * to create the value. If no fallback function is present, a new instance of the context type
     * will be created using the provided application context.
     *
     * @param context The application context to use to create the context value.
     * @param scope The scope to use for the intermediate {@link ComponentKey}.
     * @return The newly created context value.
     */
    public T create(ApplicationContext context, Scope scope) {
        T contextInstance;
        if (this.fallback != null) {
            contextInstance = this.fallback.apply(context);
        }
        else {
            throw new IllegalStateException("No fallback defined for context " + this.type.getSimpleName());
        }

        return contextInstance;
    }

    /**
     * Creates a new {@link ComponentKey} for the context type of this key. The component key
     * will be scoped to the given {@link Scope scope}.
     *
     * @param scope The scope to use for the {@link ComponentKey}.
     * @return The newly created {@link ComponentKey}.
     */
    public ComponentKey<T> componentKey(Scope scope) {
        ComponentKey.Builder<T> builder = ComponentKey.builder(this.type).scope(scope);
        if (this.name != null) {
            builder.name(this.name);
        }
        return builder.build();
    }

    /**
     * Creates a new (mutable) {@link Builder key builder} which is populated with the values
     * of this key. This will allow for the creation of a new key based on the values of this key.
     * The builder can be used to modify the values of the new key, but will never modify the values
     * of this key.
     *
     * @return A new (mutable) {@link Builder key builder} which is populated with the values of this key.
     */
    public Builder<T> mutable() {
        return new Builder<>(this);
    }

    /**
     * Creates a new {@link ContextKey key} which is bound to the given type.
     *
     * @param type The type of the context represented by the key.
     * @param <T> The type of the context represented by the key.
     *
     * @return A new {@link ContextKey key} which is bound to the given type.
     */
    public static <T extends ContextView> ContextKey<T> of(Class<T> type) {
        return builder(type).build();
    }

    /**
     * Creates a new {@link ContextKey key} which is bound to the given type.
     *
     * @param type The type of the context represented by the key.
     * @param <T> The type of the context represented by the key.
     *
     * @return A new {@link ContextKey key} which is bound to the given type.
     */
    public static <T extends ContextView> ContextKey<T> of(TypeView<T> type) {
        return builder(type).build();
    }

    /**
     * Creates a new {@link Builder key builder} which is bound to the given type and name.
     *
     * @param type The type of the context represented by the key.
     * @param <T> The type of the context represented by the key.
     *
     * @return A new {@link Builder key builder} which is bound to the given type and name.
     */
    public static <T extends ContextView> Builder<T> builder(Class<T> type) {
        return new Builder<>(type);
    }

    /**
     * Creates a new {@link Builder key builder} which is bound to the given type and name.
     *
     * @param type The type of the context represented by the key.
     * @param <T> The type of the context represented by the key.
     *
     * @return A new {@link Builder key builder} which is bound to the given type and name.
     */
    public static <T extends ContextView> Builder<T> builder(TypeView<T> type) {
        return new Builder<>(type.type());
    }

    @Override
    public String toString() {
        String nameSuffix = StringUtilities.empty(this.name) ? "" : ":" + this.name;
        return "ContextKey<%s%s>%s".formatted(this.type.getSimpleName(), nameSuffix,
                this.requiresApplicationContext() ? " (requires ApplicationContext)" : "");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        ContextKey<?> contextKey = (ContextKey<?>) other;
        return this.requiresApplicationContext == contextKey.requiresApplicationContext
                && this.type.equals(contextKey.type)
                && Objects.equals(this.name, contextKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.name, this.requiresApplicationContext);
    }

    /**
     * A mutable builder for {@link ContextKey keys}.
     *
     * @param <T> The type of the context represented by the key.
     *
     * @see ContextKey#mutable()
     * @see ContextKey#builder(Class)
     * @see ContextKey#builder(TypeView)
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Builder<T extends ContextView> {

        private final Class<T> type;
        private String name;
        private Function<ApplicationContext, T> fallback;
        private boolean requiresApplicationContext;

        /**
         * Creates a new (mutable) {@link Builder key builder} which is populated with the values
         * of the given key. This will allow for the creation of a new key based on the values of
         * the given key. The builder can be used to modify the values of the new key, but will
         * never modify the values of the given key.
         *
         * @param key The key to copy the values from.
         */
        public Builder(ContextKey<T> key) {
            this.type = key.type();
            this.name = key.name();
            this.fallback = key.fallback;
        }

        /**
         * Creates a new (mutable) {@link Builder key builder} which is bound to the given type.
         *
         * @param type The type of the context represented by the key.
         */
        public Builder(Class<T> type) {
            this.type = type;
        }

        /**
         * Sets the name of the context represented by the key. This may be empty or {@code null}
         * if the context is not named.
         *
         * @param name The name of the context represented by the key.
         *
         * @return This builder.
         */
        public Builder<T> name(String name) {
            this.name = StringUtilities.nullIfEmpty(name);
            return this;
        }

        /**
         * Sets the fallback function to use to create the context value. This function can be
         * used to create the context value if it did not exist in a context on which the key
         * is used. This supplier will indicate that the context key will not require an
         * {@link ApplicationContext} to be provided when creating the context value.
         *
         * @param fallback The fallback function to use to create the context value.
         * @return This builder.
         *
         * @see ContextKey#create(ApplicationContext)
         * @see ContextKey#create(ApplicationContext, Scope)
         */
        public Builder<T> fallback(Supplier<T> fallback) {
            this.fallback = applicationContext -> fallback.get();
            this.requiresApplicationContext = false;
            return this;
        }

        /**
         * Sets the fallback function to use to create the context value. This function can be
         * used to create the context value if it did not exist in a context on which the key
         * is used.
         *
         * @param fallback The fallback function to use to create the context value.
         * @return This builder.
         *
         * @see ContextKey#create(ApplicationContext)
         * @see ContextKey#create(ApplicationContext, Scope)
         */
        public Builder<T> fallback(Function<ApplicationContext, T> fallback) {
            this.fallback = fallback;
            this.requiresApplicationContext = true;
            return this;
        }

        /**
         * Creates a new {@link ContextKey key} based on the values of this builder.
         *
         * @return A new {@link ContextKey key} based on the values of this builder.
         */
        public ContextKey<T> build() {
            return new ContextKey<>(this);
        }
    }
}
