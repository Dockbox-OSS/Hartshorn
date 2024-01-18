package org.dockbox.hartshorn.util.introspect.convert.support.collections;

import java.util.Collection;

public interface CollectionProvider<T extends Collection<?>> {

    T createEmpty();

    T createWithCapacity(int capacity);
}
