package org.dockbox.hartshorn.util.collections;

import java.util.HashMap;

public class HashBiMap<K, V> extends StandardBiMap<K, V> {

    public HashBiMap() {
        super(new HashMap<>(), new HashMap<>());
    }
}
