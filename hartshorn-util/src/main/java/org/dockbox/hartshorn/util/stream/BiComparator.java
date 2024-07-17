package org.dockbox.hartshorn.util.stream;

public interface BiComparator<K, V> {

    int compare(K key1, V value1, K key2, V value2);

}
