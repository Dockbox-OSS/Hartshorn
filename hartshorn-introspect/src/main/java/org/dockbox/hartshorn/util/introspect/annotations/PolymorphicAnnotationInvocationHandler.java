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

package org.dockbox.hartshorn.util.introspect.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.dockbox.hartshorn.util.MapBackedAnnotationInvocationHandler;

/**
 * @deprecated Use {@link MapBackedAnnotationInvocationHandler} or {@link AnnotationAdapterProxy} instead.
 */
@Deprecated(forRemoval = true, since = "0.5.0")
public class PolymorphicAnnotationInvocationHandler implements InvocationHandler {

    Map<String, Object> cache;
    private final Class<?> type;
    private final Annotation annotation;

    public PolymorphicAnnotationInvocationHandler(Class<?> type, Annotation annotation) {
        this.type = type;
        this.annotation = annotation;
        this.cache = new HashMap<>();
    }

    public Annotation annotation() {
        return this.annotation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("annotationType".equals(method.getName())) {
            return this.type;
        }
        if ("toString".equals(method.getName())) {
            return "@" + this.type;
        }
        Object result = this.cache.get(method.getName());
        if (result != null) {
            return result;
        }

        for (Method methodInCompositeAnnotation : this.annotation.annotationType().getMethods()) {
            AttributeAlias attributeAlias = methodInCompositeAnnotation.getAnnotation(AttributeAlias.class);
            if (attributeAlias != null && (this.directAlias(attributeAlias, method) || this.indirectAlias(attributeAlias, method))) {
                result = methodInCompositeAnnotation.invoke(this.annotation);
                this.cache.put(method.getName(), result);
                return result;
            }
        }

        try {
            Object ret = this.type.getMethod(method.getName()).getDefaultValue();
            if (ret == null) {
                throw new CompositeAnnotationInvocationException(this.type, method, this.annotation);
            }
            return ret;
        }
        catch (NoSuchMethodError e) {
            throw new CompositeAnnotationInvocationException(this.type, method, this.annotation, e);
        }
    }

    private boolean directAlias(AttributeAlias attributeAlias, Method methodBeingInvoked) {
        return attributeAlias.target() == this.type && attributeAlias.value().equals(methodBeingInvoked.getName());
    }

    private boolean indirectAlias(AttributeAlias attributeAlias, Method methodBeingInvoked) {
        if (attributeAlias.target() != this.type) {
            return false;
        }
        AttributeAlias redirect = methodBeingInvoked.getAnnotation(AttributeAlias.class);
        return redirect != null && redirect.target() == Void.class && redirect.value().equals(attributeAlias.value());
    }
}
