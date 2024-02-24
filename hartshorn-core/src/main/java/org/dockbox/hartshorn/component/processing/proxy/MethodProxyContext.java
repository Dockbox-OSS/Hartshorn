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

package org.dockbox.hartshorn.component.processing.proxy;

import java.lang.annotation.Annotation;

import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.context.ApplicationAwareContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Context for {@link ServiceMethodInterceptorPostProcessor} implementations to obtain information
 * about the candidate method being processed.
 *
 * @param <T> the type of the class containing the method
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public interface MethodProxyContext<T> extends ApplicationAwareContext {

    /**
     * Returns the type of the class containing the method being processed. This is typically the
     * same type as the one provided by the {@link ComponentProcessingContext}, but may differ in
     * some cases.
     *
     * @return the type of the class containing the method
     */
    TypeView<T> type();

    /**
     * Returns the method that is being processed, no guarantees are made about whether the method
     * is compatible with the proxying process.
     *
     * @return the method being processed
     */
    MethodView<T, ?> method();

    /**
     * Returns the annotation of the given type that is present on the method being processed.
     *
     * @param annotation the type of the annotation to find
     * @param <A> the type of the annotation
     * @return the annotation instance, or {@code null} if the annotation is not present
     */
    <A extends Annotation> A annotation(Class<A> annotation);
}
