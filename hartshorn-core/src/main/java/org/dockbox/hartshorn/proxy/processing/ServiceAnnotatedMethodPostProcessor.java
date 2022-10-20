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

package org.dockbox.hartshorn.proxy.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.FunctionalComponentPostProcessor;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ServiceAnnotatedMethodPostProcessor<M extends Annotation> extends FunctionalComponentPostProcessor {

    public abstract Class<M> annotation();

    @Override
    public <T> T process(final ApplicationContext context, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {
        if (!processingContext.type().methods().annotatedWith(this.annotation()).isEmpty()) {
            final Collection<MethodView<T, ?>> methods = this.modifiableMethods(processingContext.type());

            for (final MethodView<T, ?> method : methods) {
                this.process(context, processingContext.key(), instance, method);
            }
        }
        return instance;
    }

    protected abstract <T> void process(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final MethodView<T, ?> method);

    protected <T> Collection<MethodView<T, ?>> modifiableMethods(final TypeView<T> type) {
        return type.methods().annotatedWith(this.annotation());
    }
}
