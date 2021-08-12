package org.dockbox.hartshorn.di;

import java.util.Objects;

import javax.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Key<C> {
    private final Class<C> contract;
    private final Named named;

    public static <C> Key<C> of(final Class<C> contract) {
        return new Key<>(contract, null);
    }

    public static <C> Key<C> of(final Class<C> contract, final Named named) {
        return new Key<>(contract, named);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.contract, this.named);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Key<?> key)) return false;
        return this.contract.equals(key.contract) && Objects.equals(this.named, key.named);
    }
}
