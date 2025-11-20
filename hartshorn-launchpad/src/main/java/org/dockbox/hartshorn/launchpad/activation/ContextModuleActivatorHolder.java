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

package org.dockbox.hartshorn.launchpad.activation;

import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A lazy {@link ModuleActivatorHolder} that provides activators from a
 * {@link ModuleActivatorContext} when needed.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ContextModuleActivatorHolder implements ModuleActivatorHolder {

    private final Supplier<Option<ModuleActivatorContext>> contextProvider;

    protected ContextModuleActivatorHolder(
        Supplier<Option<ModuleActivatorContext>> contextProvider
    ) {
        this.contextProvider = contextProvider;
    }

    /**
     * Create a new {@link ContextModuleActivatorHolder} with the given context provider.
     *
     * @param contextProvider the provider of the module activator context
     *
     * @return the created module activator holder
     */
    public static ContextModuleActivatorHolder of(
        Supplier<Option<ModuleActivatorContext>> contextProvider
    ) {
        return new ContextModuleActivatorHolder(contextProvider);
    }

    /**
     * Create a new {@link ContextModuleActivatorHolder} with the given context.
     *
     * @param context the module activator context
     *
     * @return the created module activator holder
     */
    public static ContextModuleActivatorHolder of(ModuleActivatorContext context) {
        return new ContextModuleActivatorHolder(() -> Option.of(context));
    }

    @Override
    public Set<Annotation> activators() {
        return this.contextProvider.get()
            .map(ModuleActivatorContext::activators)
            .orElseGet(Set::of);
    }

    @Override
    public <A> Option<A> activator(Class<A> activator) {
        return this.contextProvider.get()
            .map(context -> context.activator(activator));
    }

    @Override
    public boolean hasActivator(Class<? extends Annotation> activator) {
        return this.contextProvider.get().test(context -> context.hasActivator(activator));
    }
}
