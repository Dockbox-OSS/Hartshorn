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

package org.dockbox.hartshorn.inject.processing.proxy;

import java.lang.annotation.Annotation;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * TODO: #1060 Add documentation
 *
 * @param <M> ...
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public abstract class ServiceAnnotatedMethodPostProcessor<M extends Annotation> extends ComponentPostProcessor {

    public abstract Class<M> annotation();

    @Override
    public <T> void preConfigureComponent(InjectionCapableApplication application, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        if (processingContext.type().methods().annotatedWith(this.annotation()).isEmpty()) {
            return;
        }

        Collection<MethodView<T, ?>> methods = this.modifiableMethods(processingContext.type());

        for (MethodView<T, ?> method : methods) {
            this.process(application, processingContext.key(), instance, method);
        }
    }

    protected abstract <T> void process(InjectionCapableApplication application, ComponentKey<T> key, @Nullable T instance, MethodView<T, ?> method);

    protected <T> Collection<MethodView<T, ?>> modifiableMethods(TypeView<T> type) {
        return type.methods().annotatedWith(this.annotation());
    }
}
