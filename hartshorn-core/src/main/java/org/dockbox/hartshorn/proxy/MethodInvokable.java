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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.lang.reflect.Method;

public class MethodInvokable implements Invokable {

    private final Method method;
    private final ApplicationContext applicationContext;

    public MethodInvokable(final Method method, final ApplicationContext applicationContext) {
        this.method = method;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object invoke(final Object obj, final Object... args) throws Exception {
        if (this.method == null) return null;
        return this.method.invoke(obj, args);
    }

    @Override
    public void setAccessible(final boolean accessible) {
        this.method.setAccessible(accessible);
    }

    @Override
    public Class<?> declaringClass() {
        return this.method.getDeclaringClass();
    }

    @Override
    public String name() {
        return this.method.getName();
    }

    @Override
    public boolean isDefault() {
        return this.method.isDefault();
    }

    @Override
    public Class<?> returnType() {
        return this.method.getReturnType();
    }

    @Override
    public Class<?>[] parameterTypes() {
        return this.method.getParameterTypes();
    }

    @Override
    public String qualifiedName() {
        return this.toIntrospector().qualifiedName();
    }

    public MethodView<?, ?> toIntrospector() {
        return this.applicationContext.environment().introspect(this.method);
    }

    public Method toMethod() {
        return this.method;
    }
}
