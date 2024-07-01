/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ProxyIntrospector;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;

/**
 * An introspector for {@link Annotation} instances that are proxied, but do not have a known
 * {@link java.lang.reflect.InvocationHandler}. Such proxies are recognized by the fact that the
 * result of {@link Object#getClass()} is not equal to the result of {@link Annotation#annotationType()}.
 *
 * @param <T> The type of annotation that is proxied
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class AnnotationProxyIntrospector<T extends Annotation> implements ProxyIntrospector<T> {

    private final T annotation;

    public AnnotationProxyIntrospector(T annotation) {
        this.annotation = annotation;
    }

    @Override
    public Class<T> targetClass() {
        return TypeUtils.adjustWildcards(this.annotation.annotationType(), Class.class);
    }

    @Override
    @SuppressWarnings("GetClassOnAnnotation")
    public Class<T> proxyClass() {
        return TypeUtils.adjustWildcards(this.annotation.getClass(), Class.class);
    }

    @Override
    public T proxy() {
        return this.annotation;
    }

    @Override
    public Option<T> delegate() {
        return Option.empty();
    }
}
