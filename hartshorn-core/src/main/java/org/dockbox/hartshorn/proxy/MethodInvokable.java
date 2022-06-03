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
