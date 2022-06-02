package org.dockbox.hartshorn.proxy.cglib;

import net.sf.cglib.proxy.MethodProxy;

import org.dockbox.hartshorn.proxy.Invokable;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.Method;

public class ProxyMethodInvokable implements Invokable {

    private final MethodProxy methodProxy;
    private final Object proxy;
    private final Class<?> returnType;
    private final Class<?>[] parameterTypes;
    private final Method method;

    public ProxyMethodInvokable(final MethodProxy methodProxy, final Object proxy, final Method method) {
        this.methodProxy = methodProxy;
        this.proxy = proxy;
        this.returnType = method.getReturnType();
        this.parameterTypes = method.getParameterTypes();
        this.method = method;
    }

    @Override
    public Object invoke(final Object obj, final Object... args) throws Exception {
        try {
            return this.methodProxy.invokeSuper(obj, args);
        }
        catch (final AbstractMethodError e) {
            return TypeContext.of(this.getReturnType()).defaultOrNull();
        }
        catch (final Throwable t) {
            throw new Exception(t);
        }
    }

    @Override
    public void setAccessible(final boolean accessible) {
        // Nothing to do
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.proxy.getClass();
    }

    @Override
    public String getName() {
        return this.methodProxy.getSignature().getName();
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return this.returnType;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    @Override
    public String getQualifiedName() {
        return this.methodProxy.getSuperName();
    }
}
