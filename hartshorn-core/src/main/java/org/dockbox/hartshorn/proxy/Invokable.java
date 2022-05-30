package org.dockbox.hartshorn.proxy;

public interface Invokable {
    Object invoke(Object obj, Object... args) throws Exception;
    void setAccessible(boolean accessible);
    Class<?> getDeclaringClass();
    String getName();
    boolean isDefault();
    Class<?> getReturnType();
    Class<?>[] getParameterTypes();
    String getQualifiedName();
}
