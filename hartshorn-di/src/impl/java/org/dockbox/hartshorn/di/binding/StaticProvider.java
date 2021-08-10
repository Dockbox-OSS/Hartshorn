package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class StaticProvider<C> implements Provider<C> {

    private final Class<? extends C> target;

    @Override
    public Exceptional<C> provide() {
        return Exceptional.of(() -> this.target.getConstructor().newInstance());
    }
}
