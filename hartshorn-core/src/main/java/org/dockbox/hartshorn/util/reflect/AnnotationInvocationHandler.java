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

package org.dockbox.hartshorn.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationInvocationHandler implements InvocationHandler {

    final Map<String, Object> cache;
    private final Class<?> klass;
    private final Annotation annotation;

    public AnnotationInvocationHandler(final Class<?> klass, final Annotation annotation) {
        this.klass = klass;
        this.annotation = annotation;
        this.cache = new HashMap<>();
    }

    public Annotation annotation() {
        return this.annotation;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if ("annotationType".equals(method.getName())) {
            return this.klass;
        }
        if ("toString".equals(method.getName())) {
            return "@" + this.klass;
        }
        Object result = this.cache.get(method.getName());
        if (result != null) {
            return result;
        }

        for (final Method methodInCompositeAnnotation : this.annotation.annotationType().getMethods()) {
            final AliasFor aliasFor = methodInCompositeAnnotation.getAnnotation(AliasFor.class);
            if (aliasFor != null && (this.directAlias(aliasFor, method) || this.indirectAlias(aliasFor, method))) {
                result = methodInCompositeAnnotation.invoke(this.annotation);
                this.cache.put(method.getName(), result);
                return result;
            }
        }

        try {
            final Object ret = this.klass.getMethod(method.getName()).getDefaultValue();
            if (ret == null) {
                throw new CompositeAnnotationInvocationException(this.type, method, this.annotation);
            }
            return ret;
        }
        catch (final NoSuchMethodError e) {
            throw new CompositeAnnotationInvocationException(this.type, method, this.annotation, e);
        }
    }

    private boolean directAlias(final AliasFor aliasFor, final Method methodBeingInvoked) {
        return aliasFor.target() == this.klass && aliasFor.value().equals(methodBeingInvoked.getName());
    }

    private boolean indirectAlias(final AliasFor aliasFor, final Method methodBeingInvoked) {
        if (aliasFor.target() != this.klass) {
            return false;
        }
        final AliasFor redirect = methodBeingInvoked.getAnnotation(AliasFor.class);
        return redirect != null && redirect.target() == AliasFor.DefaultThis.class && redirect.value().equals(aliasFor.value());
    }
}
