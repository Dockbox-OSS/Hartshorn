package org.dockbox.hartshorn.util.introspect.convert.support.collections;

import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public record SupplierCollectionProvider<T extends Collection<?>>(
    Supplier<T> supplier,
    IntFunction<T> capacityConstructor
) implements CollectionProvider<T> {

    @Override
    public T createEmpty() {
        return this.supplier.get();
    }

    @Override
    public T createWithCapacity(int capacity) {
        return this.capacityConstructor.apply(capacity);
    }
}
