package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.inject.Key;

import jakarta.inject.Named;

public record ComponentKey<T>(Class<T> type, String name, Class<?> scope, boolean enable) {

    public Key<T> key() {
        return Key.of(this.type, this.name);
    }

    public static <T> Builder<T> builder(final Class<T> type) {
        return new Builder<>(type);
    }

    public static <T> Builder<T> builder(final Key<T> key) {
        return new Builder<>(key.type()).name(key.name());
    }

    public static class Builder<T> {

        private Class<T> type;
        private String name;
        private Class<?> scope;
        private boolean enable = true;

        private Builder(final Class<T> type) {
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

        public Builder<T> name(final Named named) {
            if (named != null) {
                this.name = named.value();
            }
            return this;
        }

        public Builder<T> scope(final Class<?> scope) {
            this.scope = scope;
            return this;
        }

        public Builder<T> enable(final boolean enable) {
            this.enable = enable;
            return this;
        }

        public ComponentKey<T> build() {
            return new ComponentKey<>(this.type, this.name, this.scope, this.enable);
        }
    }

}
