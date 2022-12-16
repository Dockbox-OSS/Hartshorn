/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.function.Function;
import java.util.function.Supplier;

public class ContextKey<T extends Context> {

    private final Class<T> type;
    private final String name;
    private final Supplier<T> supplier;
    private final Function<ApplicationContext, T> fallback;

    private ContextKey(final Builder<T> builder) {
        this.type = builder.type;
        this.name = builder.name;

        // Ensure only one of the two is set
        if (builder.supplier != null) {
            this.supplier = builder.supplier;
            this.fallback = null;
        }
        else {
            this.supplier = null;
            this.fallback = builder.fallback;
        }
    }

    public Class<T> type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }

    public boolean requiresApplicationContext() {
        return this.fallback != null;
    }

    public T create(final ApplicationContext context) {
        if (this.supplier != null) return this.supplier.get();
        else return this.fallback.apply(context);
    }

    public Builder<T> mutable() {
        return new Builder<>(this);
    }

    public static <T extends Context> ContextKey<T> of(final Class<T> type) {
        return builder(type).build();
    }

    public static <T extends Context> ContextKey<T> of(final TypeView<T> type) {
        return builder(type).build();
    }

    public static <T extends Context> Builder<T> builder(final Class<T> type) {
        return new Builder<>(type);
    }

    public static <T extends Context> Builder<T> builder(final TypeView<T> type) {
        return new Builder<>(type.type());
    }

    public static class Builder<T extends Context> {

        private Class<T> type;
        private String name;
        private Supplier<T> supplier;
        private Function<ApplicationContext, T> fallback;

        public Builder(final ContextKey<T> key) {
            this.type = key.type();
            this.name = key.name();
            this.fallback = key.fallback;
            this.supplier = key.supplier;
        }

        public Builder(final Class<T> type) {
            this.type = type;
        }

        public Builder<T> type(final Class<T> type) {
            this.type = type;
            return this;
        }

        public Builder<T> name(final String name) {
            this.name = name;
            return this;
        }

        public Builder<T> fallback(final Supplier<T> fallback) {
            this.supplier = fallback;
            this.fallback = null;
            return this;
        }

        public Builder<T> fallback(final Function<ApplicationContext, T> fallback) {
            this.fallback = fallback;
            this.supplier = null;
            return this;
        }

        public ContextKey<T> build() {
            return new ContextKey<>(this);
        }
    }
}
