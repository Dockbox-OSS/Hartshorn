/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.proxy.cglib;

import net.sf.cglib.proxy.MethodProxy;

import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.Method;

/**
 * @deprecated CGLib is not actively maintained, and commonly causes issues with Java 9+.
 *             It is recommended to use Javassist instead, through the
 *             {@code org.dockbox.hartshorn.proxy.javassist.JavassistProxyMethodHandler}.
 */
@Deprecated(since = "0.4.13")
public class CglibProxyMethodInvokable implements Invokable {

    private final MethodProxy methodProxy;
    private final Object proxy;
    private final Class<?>[] parameterTypes;
    private final Method method;
    private final TypeView<?> returnType;

    public CglibProxyMethodInvokable(Introspector introspector, MethodProxy methodProxy, Object proxy, Method method) {
        this.methodProxy = methodProxy;
        this.proxy = proxy;
        this.parameterTypes = method.getParameterTypes();
        this.method = method;
        this.returnType = introspector.introspect(method.getReturnType());
    }

    @Override
    public Object invoke(Object obj, Object... args) throws Exception {
        try {
            return this.methodProxy.invokeSuper(obj, args);
        }
        catch (AbstractMethodError e) {
            return this.returnType.defaultOrNull();
        }
        catch (Exception e) {
            throw e;
        }
        catch (Throwable t) {
            throw new Exception(t);
        }
    }

    @Override
    public void setAccessible(boolean accessible) {
        // Nothing to do. Setting the local method accessible does not affect the proxy method.
    }

    @Override
    public Class<?> declaringClass() {
        return this.proxy.getClass();
    }

    @Override
    public String name() {
        return this.methodProxy.getSignature().getName();
    }

    @Override
    public boolean isDefault() {
        return this.method.isDefault();
    }

    @Override
    public Class<?> returnType() {
        return this.returnType.type();
    }

    @Override
    public Class<?>[] parameterTypes() {
        return this.parameterTypes;
    }

    @Override
    public String qualifiedName() {
        return this.methodProxy.getSuperName();
    }
}
