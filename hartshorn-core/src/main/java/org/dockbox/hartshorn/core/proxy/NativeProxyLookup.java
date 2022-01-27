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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.AnnotationHelper.AnnotationInvocationHandler;
import org.dockbox.hartshorn.core.proxy.javassist.JavassistInterfaceHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class NativeProxyLookup implements ProxyLookup {

    @Override
    public <T> Class<T> unproxy(final T instance) {
        final InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (invocationHandler instanceof JavassistInterfaceHandler proxyInterfaceHandler) {
            return proxyInterfaceHandler.handler().type().type();
        }
        else if (invocationHandler instanceof AnnotationInvocationHandler annotationInvocationHandler) {
            return (Class<T>) annotationInvocationHandler.annotation().annotationType();
        }
        else if (instance instanceof Annotation annotation) {
            return (Class<T>) annotation.annotationType();
        }
        return instance != null ? (Class<T>) instance.getClass() : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return instance != null && this.isProxy(instance.getClass());
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return Proxy.isProxyClass(candidate);
    }
}
