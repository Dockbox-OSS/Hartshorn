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

package org.dockbox.hartshorn.component.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentType;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.Key;

import java.lang.annotation.Annotation;

public abstract class FunctionalComponentPostProcessor<A extends Annotation> implements ComponentPostProcessor<A> {

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {
        final ComponentContainer container = processingContext.get(Key.of(ComponentContainer.class));
        if (container.componentType() != ComponentType.FUNCTIONAL) {
            return false;
        }
        return ComponentPostProcessor.super.preconditions(context, key, instance, processingContext);
    }
}
