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

import org.dockbox.hartshorn.util.Result;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationAdapterProxy<A extends Annotation> implements InvocationHandler {
    private final Annotation actual;
    private final Class<A> targetAnnotationClass;
    private final LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy;
    private final Map<String, Result<Object>> methodsCache = new ConcurrentHashMap<>();

    AnnotationAdapterProxy(final Annotation actual, final Class<A> targetAnnotationClass, final LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy) {
        this.actual = actual;
        this.targetAnnotationClass = targetAnnotationClass;
        this.actualAnnotationHierarchy = actualAnnotationHierarchy;
    }

    Annotation actual() {
        return this.actual;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        if (method.getDeclaringClass() == AnnotationAdapter.class) {
            return this.actual;
        }

        if ("hashCode".equals(method.getName())) {
            return AnnotationHelper.actualAnnotationBehindProxy(this.actual).hashCode();
        }

        if ("equals".equals(method.getName()) && method.getParameters().length == 1) {
            if (args[0] instanceof Annotation) {
                return this.actual.equals(AnnotationHelper.actualAnnotationBehindProxy((Annotation) args[0]));
            }
            else {
                return this.actual.equals(args[0]);
            }
        }

        Result<Object> cachedField = this.methodsCache.get(method.getName());
        if (cachedField == null) {
            cachedField = AnnotationHelper.searchInHierarchy(this.actual, this.targetAnnotationClass, this.actualAnnotationHierarchy, method.getName());
            this.methodsCache.put(method.getName(), cachedField);
        }
        return cachedField.orNull();
    }
}
