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
import org.dockbox.hartshorn.application.ModifiableActivatorHolder;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.StandardComponentProvider;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClasspathApplicationContext extends DelegatingApplicationContext implements ProcessableApplicationContext {

    public static Comparator<String> PREFIX_PRIORITY_COMPARATOR = Comparator.naturalOrder();

    protected transient MultiMap<Integer, ComponentPreProcessor> preProcessors;
    protected transient Queue<String> prefixQueue;

    public ClasspathApplicationContext(final InitializingContext context) {
        super(context);
        this.environment().annotationsWith(context.configuration().activator(), ServiceActivator.class).forEach(this::addActivator);
        this.log().debug("Located %d service activators".formatted(this.activators().size()));
    }

    @Override
    protected void prepareInitialization() {
        this.preProcessors = new ConcurrentSetTreeMultiMap<>();
        this.prefixQueue = new PriorityQueue<>(PREFIX_PRIORITY_COMPARATOR);
    }

    @Override
    public void addActivator(final Annotation annotation) {
        this.checkRunning();
        if (this.activatorHolder() instanceof ModifiableActivatorHolder modifiable) {
            modifiable.addActivator(annotation);
            return;
        }
        throw new IllegalModificationException("Activator holder is not modifiable");
    }

    @Override
    public void add(final ComponentProcessor processor) {
        this.checkRunning();

        final Integer order = processor.order();
        final String name = TypeContext.of(processor).name();

        if (processor instanceof ComponentPostProcessor postProcessor && this.componentProvider() instanceof StandardComponentProvider provider) {
            // Singleton binding is decided by the component provider, to allow for further optimization
            provider.postProcessor(postProcessor);
            this.log().debug("Added " + name + " for component post-processing at phase " + order);
        }
        else if (processor instanceof ComponentPreProcessor preProcessor) {
            this.preProcessors.put(preProcessor.order(), preProcessor);
            this.bind((Class<ComponentPreProcessor>) preProcessor.getClass()).singleton(preProcessor);
            this.log().debug("Added " + name + " for component pre-processing at phase " + order);
        }
        else {
            this.log().warn("Unsupported component processor type [" + name + "]");
        }
    }

    protected void processPrefixQueue() {
        this.checkRunning();
        String next;
        while ((next = this.prefixQueue.poll()) != null) {
            this.processPrefix(next);
        }
    }

    protected void processPrefix(final String prefix) {
        this.checkRunning();
        this.locator().register(prefix);
    }

    @Override
    public void process() {
        this.checkRunning();
        this.processPrefixQueue();
        final Collection<ComponentContainer> containers = this.locator().containers();
        this.log().debug("Located %d components from classpath".formatted(containers.size()));
        this.process(containers);
        this.isRunning = true;
    }

    protected void process(final Collection<ComponentContainer> containers) {
        this.checkRunning();
        for (final ComponentPreProcessor serviceProcessor : this.preProcessors.allValues()) {
            for (final ComponentContainer container : containers) {
                final TypeContext<?> service = container.type();
                final Key<?> key = Key.of(service);
                if (serviceProcessor.modifies(this, key)) {
                    this.log().debug("Processing component %s with registered processor %s".formatted(container.id(), TypeContext.of(serviceProcessor).name()));
                    serviceProcessor.process(this, key);
                }
            }
            if (serviceProcessor instanceof ExitingComponentProcessor exiting) {
                exiting.exit(this);
            }
        }
    }

    @Override
    public void bind(final String prefix) {
        this.checkRunning();
        for (final String scannedPrefix : this.environment().prefixContext().prefixes()) {
            if (prefix.startsWith(scannedPrefix)) return;
            if (scannedPrefix.startsWith(prefix)) {
                // If a previously scanned prefix is a prefix of the current prefix, it is more specific and should be ignored,
                // as this prefix will include the specific prefix.
                this.environment().prefixContext().prefixes().remove(scannedPrefix);
            }
        }
        this.environment().prefix(prefix);
        this.prefixQueue.add(prefix);
    }
}
