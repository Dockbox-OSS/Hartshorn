/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.Invokable;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.Method;

public class ProxyMethodInvokable implements Invokable {

    private final MethodProxy methodProxy;
    private final Object proxy;
    private final Class<?>[] parameterTypes;
    private final Method method;
    private final TypeView<?> returnType;

    public ProxyMethodInvokable(final ApplicationContext applicationContext, final MethodProxy methodProxy, final Object proxy, final Method method) {
        this.methodProxy = methodProxy;
        this.proxy = proxy;
        this.parameterTypes = method.getParameterTypes();
        this.method = method;
        this.returnType = applicationContext.environment().introspect(method.getReturnType());
    }

    @Override
    public Object invoke(final Object obj, final Object... args) throws Exception {
        try {
            return this.methodProxy.invokeSuper(obj, args);
        }
        catch (final AbstractMethodError e) {
            return this.returnType.defaultOrNull();
        }
        catch (final Exception e) {
            throw e;
        }
        catch (final Throwable t) {
            throw new Exception(t);
        }
    }

    @Override
    public void setAccessible(final boolean accessible) {
        // Nothing to do. Setting the local method accessible does not affect the proxy method.
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
        return this.method.isDefault();
    }

    @Override
    public Class<?> getReturnType() {
        return this.returnType.type();
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
