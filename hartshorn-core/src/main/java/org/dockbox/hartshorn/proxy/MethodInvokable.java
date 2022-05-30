package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.util.reflect.MethodContext;

import java.lang.reflect.Method;

public class MethodInvokable implements Invokable {

    private final Method method;

    public MethodInvokable(final Method method) {
        this.method = method;
    }

    @Override
    public Object invoke(final Object obj, final Object... args) throws Exception {
        return this.method.invoke(obj, args);
    }

    @Override
    public void setAccessible(final boolean accessible) {
        this.method.setAccessible(accessible);
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.method.getDeclaringClass();
    }

    @Override
    public String getName() {
        return this.method.getName();
    }

    @Override
    public boolean isDefault() {
        return this.method.isDefault();
    }

    @Override
    public Class<?> getReturnType() {
        return this.method.getReturnType();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return this.method.getParameterTypes();
    }

    @Override
    public String getQualifiedName() {
        return this.toMethodContext().qualifiedName();
    }

    public MethodContext<?, ?> toMethodContext() {
        return MethodContext.of(this.method);
    }

    public Method toMethod() {
        return this.method;
    }
}
