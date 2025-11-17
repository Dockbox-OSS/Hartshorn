/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.inject.processing.proxy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * An abstract {@link ComponentPostProcessor} that allows implementations to process methods annotated with a specific
 * annotation, during the pre-configuration phase. This can be particularly useful for proxy generation or method
 * interception based on annotations.
 *
 * @param <M> the type of the annotation that this post processor processes
 *
 * @see AnnotatedMethodInterceptorPostProcessor
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public abstract class AnnotatedMethodPostProcessor<M extends Annotation> extends ComponentPostProcessor {

    /**
     * Returns the annotation that should be present on methods to be processed by this post processor.
     *
     * @return the annotation class
     */
    public abstract Class<M> annotation();

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return !processingContext.type().methods().annotatedWith(this.annotation()).isEmpty();
    }

    @Override
    public <T> void preConfigureComponent(InjectionCapableApplication application, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        Collection<MethodView<T, ?>> methods = this.modifiableMethods(processingContext.type());

        for (MethodView<T, ?> method : methods) {
            this.process(application, processingContext.key(), instance, method);
        }
    }

    /**
     * Processes a method annotated with the specified annotation.
     *
     * @param application the injection-capable application
     * @param key the component key
     * @param instance the component instance, if available
     * @param method the method to process
     *
     * @param <T> the type of the component
     */
    protected abstract <T> void process(InjectionCapableApplication application, ComponentKey<T> key, @Nullable T instance, MethodView<T, ?> method);

    /**
     * Returns the methods that may be modified by this post processor.
     *
     * @param type the type to retrieve methods from
     *
     * @param <T> the type of the component
     *
     * @return the modifiable methods
     */
    protected <T> Collection<MethodView<T, ?>> modifiableMethods(TypeView<T> type) {
        return type.methods().annotatedWith(this.annotation());
    }
}
