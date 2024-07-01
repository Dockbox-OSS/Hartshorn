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
 * An introspector for {@link Annotation} instances that are proxied with a {@link AnnotationAdapterProxy} handler.
 *
 * @param <T> The type of annotation that is proxied
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class AnnotationAdapterProxyIntrospector<T extends Annotation> implements ProxyIntrospector<T> {

    private final Annotation annotation;
    private final AnnotationAdapterProxy<T> adapterProxy;

    public AnnotationAdapterProxyIntrospector(Annotation annotation, AnnotationAdapterProxy<T> adapterProxy) {
        this.annotation = annotation;
        this.adapterProxy = adapterProxy;
    }

    @Override
    public Class<T> targetClass() {
        return this.adapterProxy.targetAnnotationClass();
    }

    @Override
    @SuppressWarnings("GetClassOnAnnotation")
    public Class<T> proxyClass() {
        return TypeUtils.adjustWildcards(this.annotation.getClass(), Class.class);
    }

    @Override
    public T proxy() {
        return this.adapterProxy.targetAnnotationClass().cast(this.annotation);
    }

    @Override
    public Option<T> delegate() {
        return Option.of(this.adapterProxy.targetAnnotationClass().cast(this.adapterProxy.actualAnnotation()));
    }
}
