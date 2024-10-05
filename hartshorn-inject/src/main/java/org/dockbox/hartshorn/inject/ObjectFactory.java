package org.dockbox.hartshorn.inject;

@FunctionalInterface
public interface ObjectFactory {

    <T> T create(Class<T> type);
}
