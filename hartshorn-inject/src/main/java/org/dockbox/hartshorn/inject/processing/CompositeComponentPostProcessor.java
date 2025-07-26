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

package org.dockbox.hartshorn.inject.processing;

import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.function.CheckedConsumer;

/**
 * A {@link ComponentPostProcessor} which delegates to a collection of other post processors. The post processors are
 * ordered by priority, and will be executed in order of priority. If multiple post processors have the same priority,
 * they will be executed in the order defined by the provided {@link MultiMap} implementation.
 *
 * <p>As this processor is a composite, it is able to call each phase of the component lifecycle on each of the
 * post processors immediately, instead of having each post processor call their own lifecycle phases. This allows for
 * more efficient and predictable execution of the component lifecycle, as all pre-configuration phases will be complete
 * before the initialization phase begins, and all initialization phases will be complete before the post-configuration
 * phase begins.
 *
 * <p>In summary, given two post processors, {@code A} and {@code B}, with priorities {@code 1} and {@code 2}
 * respectively, the following will occur:
 *
 * <ol>
 *     <li>{@code A.preConfigureComponent()} will be called</li>
 *     <li>{@code B.preConfigureComponent()} will be called</li>
 *     <li>{@code A.initializeComponent()} will be called</li>
 *     <li>{@code B.initializeComponent()} will be called</li>
 *     <li>{@code A.postConfigureComponent()} will be called</li>
 *     <li>{@code B.postConfigureComponent()} will be called</li>
 * </ol>
 *
 * <p>Comparing this to the alternative, where each post processor calls its own lifecycle phases, the following would
 * occur:
 *
 * <ol>
 *     <li>{@code A.preConfigureComponent()} will be called</li>
 *     <li>{@code A.initializeComponent()} will be called</li>
 *     <li>{@code A.postConfigureComponent()} will be called</li>
 *     <li>{@code B.preConfigureComponent()} will be called</li>
 *     <li>{@code B.initializeComponent()} will be called</li>
 *     <li>{@code B.postConfigureComponent()} will be called</li>
 * </ol>
 *
 * <p>If no processors are provided, the {@link #preConfigureComponent(InjectionCapableApplication, Object, ComponentProcessingContext)}
 * and {@link #postConfigureComponent(InjectionCapableApplication, Object, ComponentProcessingContext)} methods are effectively no-ops,
 * and the {@link #initializeComponent(InjectionCapableApplication, Object, ComponentProcessingContext)} method will simply return the
 * provided instance. This is to ensure that the composite processor can be used in place of a {@link ComponentPostProcessor} without any
 * unexpected side effects.
 *
 * @see ComponentPostProcessor
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CompositeComponentPostProcessor extends ComponentPostProcessor {

    private final Supplier<MultiMap<Integer, ComponentPostProcessor>> postProcessors;

    public CompositeComponentPostProcessor(Supplier<MultiMap<Integer, ComponentPostProcessor>> postProcessors) {
        this.postProcessors = postProcessors;
    }

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        MultiMap<Integer, ComponentPostProcessor> processors = this.postProcessors.get();
        if (processors.isEmpty()) {
            // If no processors are available, we consider this compatible, as the composite processor
            // will not perform any actions.
            return true;
        }
        // Doesn't need to be thread-safe, this will only be retained during the processing of a single component.
        MultiMap<Integer, ComponentPostProcessor> compatibleProcessors = new ArrayListMultiMap<>();
        for (Integer priority : processors.keySet()) {
            for (ComponentPostProcessor postProcessor : processors.get(priority)) {
                if (postProcessor.isCompatible(processingContext)) {
                    compatibleProcessors.put(priority, postProcessor);
                }
            }
        }
        if (compatibleProcessors.isEmpty()) {
            return false;
        }
        // Store, so that we can use these processors later in the processing execution.
        processingContext.addContext(new CompatibleProcessorsContext(compatibleProcessors));
        return true;
    }

    @Override
    public <T> void preConfigureComponent(
        InjectionCapableApplication application,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) throws ApplicationException {
        this.withProcessors(this.resolveCompatibleProcessors(processingContext), processor -> {
            processor.preConfigureComponent(application, instance, processingContext);
        });
    }

    @Override
    public <T> T initializeComponent(
        InjectionCapableApplication application,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) throws ApplicationException {
        MultiMap<Integer, ComponentPostProcessor> processors = this.resolveCompatibleProcessors(processingContext);
        for (Integer priority : processors.keySet()) {
            for (ComponentPostProcessor postProcessor : processors.get(priority)) {
                instance = postProcessor.initializeComponent(application, instance, processingContext);
            }
        }
        return instance;
    }

    @Override
    public <T> void postConfigureComponent(
        InjectionCapableApplication application,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) throws ApplicationException {
        this.withProcessors(this.resolveCompatibleProcessors(processingContext), processor -> {
            processor.postConfigureComponent(application, instance, processingContext);
        });
    }

    private void withProcessors(MultiMap<Integer, ComponentPostProcessor> processors, CheckedConsumer<ComponentPostProcessor> processor) throws ApplicationException {
        for (Integer priority : processors.keySet()) {
            for (ComponentPostProcessor postProcessor : processors.get(priority)) {
                processor.accept(postProcessor);
            }
        }
    }

    private MultiMap<Integer, ComponentPostProcessor> resolveCompatibleProcessors(ContextView context) {
        return context.firstContext(CompatibleProcessorsContext.class).orElseThrow(() -> {
            return new IllegalStateException("No compatible processors context found for context: " + context + ". Was the isCompatible method called?");
        }).processors();
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
