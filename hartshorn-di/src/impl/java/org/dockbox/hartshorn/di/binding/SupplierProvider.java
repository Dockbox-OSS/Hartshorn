package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;

import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SupplierProvider<C> implements Provider<C> {

    private final Supplier<C> supplier;

    @Override
    public Exceptional<C> provide() {
        return Exceptional.of(this.supplier::get);
    }
}
