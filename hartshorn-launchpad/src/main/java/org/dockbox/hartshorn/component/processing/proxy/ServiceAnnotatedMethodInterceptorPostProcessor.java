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
import java.util.Collection;

import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

/**
 * TODO: #1060 Add documentation
 *
 * @param <M> ...
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public abstract class ServiceAnnotatedMethodInterceptorPostProcessor<M extends Annotation> extends ServiceMethodInterceptorPostProcessor {

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return !processingContext.type().methods().annotatedWith(this.annotation()).isEmpty();
    }

    public abstract Class<M> annotation();

    @Override
    protected <T> Collection<MethodView<T, ?>> modifiableMethods(ComponentProcessingContext<T> processingContext) {
        return processingContext.type().methods().annotatedWith(this.annotation());
    }
}
