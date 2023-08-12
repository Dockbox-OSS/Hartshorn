package org.dockbox.hartshorn.util.collections;

public interface BiMultiMap<K, V> extends MultiMap<K, V> {

    MultiMap<V, K> inverse();

}
