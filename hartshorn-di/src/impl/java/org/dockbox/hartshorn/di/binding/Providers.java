package org.dockbox.hartshorn.di.binding;

import java.util.function.Supplier;

public class Providers {

    public static <C> Provider<C> of(final Class<? extends C> type) {
        return new StaticProvider<>(type);
    }

    public static <C> Provider<C> of(final C instance) {
        return new InstanceProvider<>(instance);
    }

    public static <C> Provider<C> of(final Supplier<C> supplier) {
        return new SupplierProvider<>(supplier);
    }

}
