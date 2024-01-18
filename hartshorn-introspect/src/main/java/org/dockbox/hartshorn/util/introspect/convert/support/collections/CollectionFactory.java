package org.dockbox.hartshorn.util.introspect.convert.support.collections;

import java.util.Collection;

public interface CollectionFactory {

    <O extends Collection<E>, E> O createCollection(Class<O> targetType, Class<E> elementType);

    <O extends Collection<E>, E> O createCollection(Class<O> targetType, Class<E> elementType, int length);

}
