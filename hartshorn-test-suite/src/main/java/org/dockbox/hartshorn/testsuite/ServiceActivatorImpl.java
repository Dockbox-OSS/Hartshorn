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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceActivatorImpl implements ServiceActivator {

    private Set<String> packages = ConcurrentHashMap.newKeySet();
    private Set<Class<? extends ComponentProcessor<?>>> processors = ConcurrentHashMap.newKeySet();

    public boolean addPackages(final Collection<String> packages) {
        return this.packages.addAll(packages);
    }

    public boolean addPackages(final String... packages) {
        return this.packages.addAll(Arrays.asList(packages));
    }

    public boolean addPackage(final String pkg) {
        return this.packages.add(pkg);
    }

    public boolean addProcessors(final Collection<Class<? extends ComponentProcessor<?>>> processors) {
        return this.processors.addAll(processors);
    }

    @SafeVarargs
    public final boolean addProcessors(final Class<? extends ComponentProcessor<?>>... processors) {
        return this.processors.addAll(Arrays.asList(processors));
    }

    public boolean addProcessor(final Class<? extends ComponentProcessor<?>> processor) {
        return this.processors.add(processor);
    }

    @Override
    public String[] scanPackages() {
        return this.packages.toArray(new String[0]);
    }

    @Override
    public Class<? extends ComponentProcessor<?>>[] processors() {
        return this.processors.toArray(new Class[0]);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ServiceActivator.class;
    }
}
