package org.dockbox.hartshorn.core.domain;

import java.util.concurrent.ConcurrentHashMap;

public class TypeMap<T> extends ConcurrentHashMap<Class<T>, T> {
}
