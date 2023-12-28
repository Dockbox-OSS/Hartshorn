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

package org.dockbox.hartshorn.proxy.advice.intercept;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.lang.reflect.Method;

/**
 * Implementation of {@link Invokable} that is backed by a {@link Method}. Invoking the method is delegated to the
 * {@link Method#invoke(Object, Object...)} method.
 *
 * @since 0.4.13
 * @author Guus Lieben
 */
public class MethodInvokable implements Invokable {

    private final Method method;
    private final Introspector introspector;

    public MethodInvokable(Method method, Introspector introspector) {
        this.method = method;
        this.introspector = introspector;
    }

    @Override
    public Object invoke(Object obj, Object... args) throws Exception {
        if (this.method == null) {
            return null;
        }
        return this.method.invoke(obj, args);
    }

    @Override
    public void setAccessible(boolean accessible) {
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

    /**
     * Returns a {@link MethodView} for the underlying method.
     *
     * @return a {@link MethodView} for the underlying method
     */
    public MethodView<?, ?> toIntrospector() {
        return this.introspector.introspect(this.method);
    }

    /**
     * Returns the underlying method.
     *
     * @return the underlying method
     */
    public Method toMethod() {
        return this.method;
    }
}
