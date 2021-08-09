package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InstanceProvider<T> implements Provider<T> {

    private final T instance;

    @Override
    public Exceptional<T> provide() {
        return Exceptional.of(this.instance);
    }
}
