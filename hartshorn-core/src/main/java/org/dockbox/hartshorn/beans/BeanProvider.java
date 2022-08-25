package org.dockbox.hartshorn.beans;

import java.util.List;

public interface BeanProvider {
    <T> T first(Class<T> type);

    <T> T first(Class<T> type, String id);

    <T> List<T> all(Class<T> type);

    <T> List<T> all(Class<T> type, String id);
}
