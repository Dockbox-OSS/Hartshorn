package org.dockbox.hartshorn.core.proxy;

@FunctionalInterface
public interface NameGenerator {
    String get(Class<?> type);
}
