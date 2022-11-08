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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.application.InitializingContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.StandardComponentProvider;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.component.processing.ProcessingPhase;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClasspathApplicationContext extends DelegatingApplicationContext implements ProcessableApplicationContext, ObserverApplicationContext {

    public static Comparator<String> PREFIX_PRIORITY_COMPARATOR = Comparator.naturalOrder();

    protected transient MultiMap<Integer, ComponentPreProcessor> preProcessors;
    protected transient Queue<String> prefixQueue;

    public ClasspathApplicationContext(final InitializingContext context) {
        super(context);
    }

    @Override
    protected void prepareInitialization() {
        this.preProcessors = new ConcurrentSetTreeMultiMap<>();
        this.prefixQueue = new PriorityQueue<>(PREFIX_PRIORITY_COMPARATOR);
    }

    @Override
    public void add(final ComponentProcessor processor) {
        this.checkRunning();

        final Integer order = processor.order();

        String phase = "unknown";
        for (final ProcessingPhase processingPhase : ProcessingOrder.PHASES) {
            if (processingPhase.test(order)) {
                phase = processingPhase.name();
                break;
            }
        }

        final String name = processor.getClass().getSimpleName();

        if (processor instanceof ComponentPostProcessor postProcessor && this.componentProvider() instanceof StandardComponentProvider provider) {
            // Singleton binding is decided by the component provider, to allow for further optimization
            provider.postProcessor(postProcessor);
            this.log().debug("Added %s for component post-processing during %s phase (order %d)".formatted(name, phase.toLowerCase(), order));
        }
        else if (processor instanceof ComponentPreProcessor preProcessor) {
            this.preProcessors.put(preProcessor.order(), preProcessor);
            this.bind((Class<ComponentPreProcessor>) preProcessor.getClass()).singleton(preProcessor);
            this.log().debug("Added %s for component pre-processing during %s phase (order %d)".formatted(name, phase.toLowerCase(), order));
        }
        else {
            this.log().warn("Unsupported component processor type [" + name + "]");
        }
    }

    @Override
    public void loadContext() {
        this.checkRunning();
        final Collection<ComponentContainer> containers = this.locator().containers();
        this.log().debug("Located %d components".formatted(containers.size()));
        this.processComponents(containers);
        this.isRunning = true;
    }

    protected void processComponents(final Collection<ComponentContainer> containers) {
        this.checkRunning();
        for (final ComponentPreProcessor serviceProcessor : this.preProcessors.allValues()) {
            this.log().debug("Processing %s components with registered processor %s".formatted(containers.size(), serviceProcessor.getClass().getSimpleName()));
            for (final ComponentContainer container : containers) {
                final TypeView<?> service = container.type();
                final Key<?> key = Key.of(service);
                final ComponentProcessingContext<?> context = new ComponentProcessingContext<>(this, key, null);
                this.log().debug("Processing component %s with registered processor %s".formatted(container.id(), serviceProcessor.getClass().getSimpleName()));
                serviceProcessor.process(context);
            }
            if (serviceProcessor instanceof ExitingComponentProcessor exiting) {
                exiting.exit(this);
            }
        }
    }

    @Override
    public void componentAdded(final ComponentContainer container) {
        for (final ComponentPreProcessor serviceProcessor : this.preProcessors.allValues()) {
            this.log().debug("Processing standalone component %s with registered processor %s".formatted(container.id(), serviceProcessor.getClass().getSimpleName()));
            final TypeView<?> service = container.type();
            final Key<?> key = Key.of(service);
            final ComponentProcessingContext<?> context = new ComponentProcessingContext<>(this, key, null);
            serviceProcessor.process(context);
            if (serviceProcessor instanceof ExitingComponentProcessor exiting) {
                exiting.exit(this);
            }
        }
    }
}
