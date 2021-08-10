package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class InstanceProvider<T> implements Provider<T> {

    private final T instance;

    @Override
    public Exceptional<T> provide() {
        return Exceptional.of(this.instance);
    }
}
