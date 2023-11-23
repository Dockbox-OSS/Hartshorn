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

package org.dockbox.hartshorn.component.processing;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.collections.MultiMap;

public class CompositeComponentPostProcessor extends ComponentPostProcessor {

    private final Supplier<MultiMap<Integer, ComponentPostProcessor>> postProcessors;

    public CompositeComponentPostProcessor(Supplier<MultiMap<Integer, ComponentPostProcessor>> postProcessors) {
        this.postProcessors = postProcessors;
    }

    @Override
    public <T> void preConfigureComponent(
        ApplicationContext context,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        this.withProcessors(processor -> {
            processor.preConfigureComponent(context, instance, processingContext);
        });
    }

    @Override
    public <T> T initializeComponent(
        ApplicationContext context,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        MultiMap<Integer, ComponentPostProcessor> processors = this.postProcessors.get();
        if (processors.isEmpty()) {
            return instance;
        }
        return this.withProcessors(processors, processor -> processor.initializeComponent(context, instance, processingContext));
    }

    @Override
    public <T> void postConfigureComponent(
        ApplicationContext context,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        this.withProcessors(processor -> {
            processor.postConfigureComponent(context, instance, processingContext);
        });
    }

    private void withProcessors(Consumer<ComponentPostProcessor> consumer) {
        this.withProcessors(this.postProcessors.get(), processor -> {
            consumer.accept(processor);
            return null;
        });
    }

    private <T> T withProcessors(MultiMap<Integer, ComponentPostProcessor> processors, Function<ComponentPostProcessor, T> processor) {
        T result = null;
        for (Integer priority : processors.keySet()) {
            for (ComponentPostProcessor postProcessor : processors.get(priority)) {
                result = processor.apply(postProcessor);
            }
        }
        return result;
    }
}
