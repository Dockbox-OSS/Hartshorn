package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.util.reflect.TypeContext;

public record BeanReference<T>(T bean, TypeContext<T> type, String id) {
}
