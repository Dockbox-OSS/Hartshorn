package org.dockbox.hartshorn.core.proxy.javassist;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@FunctionalInterface
public interface MethodInvoker {
    Object invoke(Method method) throws InvocationTargetException, IllegalAccessException;
}
