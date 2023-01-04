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

package org.dockbox.hartshorn.proxy;

public interface ProxyObject<T> {

    default boolean isEqualsMethod(final Invokable invokable) {
        return "equals".equals(invokable.name())
                && invokable.declaringClass().equals(Object.class)
                && invokable.returnType().equals(boolean.class)
                && invokable.parameterTypes().length == 1
                && invokable.parameterTypes()[0].equals(Object.class);
    }

    default boolean isToStringMethod(final Invokable invokable) {
        return "toString".equals(invokable.name())
                && invokable.declaringClass().equals(Object.class)
                && invokable.parameterTypes().length == 0
                && invokable.returnType().equals(String.class);
    }

    default boolean isHashCodeMethod(final Invokable invokable) {
        return "hashCode".equals(invokable.name())
                && invokable.declaringClass().equals(Object.class)
                && invokable.parameterTypes().length == 0
                && invokable.returnType().equals(int.class);
    }

    default boolean proxyEquals(final Object obj) {
        if (obj == null) return false;
        if (Boolean.TRUE.equals(this.manager().delegate()
                .map(instance -> instance.equals(obj))
                .orElse(false))
        ) return true;
        return this.manager().proxy() == obj;
    }

    default String proxyToString(final T self) {
        if (self == null) return "null";
        final String canonicalName = this.manager().targetClass().getCanonicalName();
        return "Proxy: " + canonicalName + "@" + Integer.toHexString(this.proxyHashCode(self));
    }

    default int proxyHashCode(final T self) {
        return System.identityHashCode(self);
    }

    ProxyManager<T> manager();
}
