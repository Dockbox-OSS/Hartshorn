package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;

import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class SupplierProvider<C> implements Provider<C> {

    private final Supplier<C> supplier;

    @Override
    public Exceptional<C> provide() {
        return Exceptional.of(this.supplier::get);
    }
}
