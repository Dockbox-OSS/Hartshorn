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

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.util.collections.NavigableMultiMap;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

/**
 * A registry for {@link ComponentProcessor} instances. This registry is used to store and manage
 * all processors that are registered within a given environment (typically a
 * {@link ComponentProvider} or {@link InjectionCapableApplication}).
 *
 * <p>Note that the registry itself is not responsible for the actual processing of components. It
 * is merely a storage
 * mechanism for processors.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public interface ComponentProcessorRegistry {

    /**
     * Registers a {@link ComponentProcessor} with this registry. If the processor was previously
     * registered lazily, this will replace the lazy registration with the provided instance.
     *
     * @param processor the processor to register
     */
    void register(ComponentProcessor processor);

    /**
     * Unregisters a {@link ComponentProcessor} from this registry. If the processor is not
     * registered, this method has no effect. This does not affect lazy registrations.
     *
     * @param processor the processor to unregister
     */
    void unregister(ComponentProcessor processor);

    /**
     * Registers a {@link ComponentPostProcessor} lazily with this registry. The processor will be
     * instantiated when it is first looked up.
     *
     * @param componentProcessor the type of processor to register lazily
     */
    void registerLazy(Class<? extends ComponentPostProcessor> componentProcessor);

    /**
     * Checks if a {@link ComponentProcessor} exactly matching the given type is registered with
     * this registry. This includes both eagerly and lazily registered processors.
     *
     * @param componentProcessor the type of processor to check for
     *
     * @return {@code true} if a processor of the given type is registered, {@code false} otherwise
     */
    boolean isRegistered(Class<? extends ComponentProcessor> componentProcessor);

    /**
     * Looks up a {@link ComponentProcessor} exactly matching the given type in this registry. If no
     * processor of the given type is registered, an empty {@link Option} is returned.
     *
     * @param componentProcessor the type of processor to look up
     * @param <T> the type of the processor
     *
     * @return an {@link Option} containing the processor, if it is registered
     */
    <T extends ComponentProcessor> Option<T> lookup(Class<T> componentProcessor);

    /**
     * Returns a set of all registered {@link ComponentProcessor} instances. This does not include
     * lazily registered processors that have not yet been instantiated.
     *
     * @return a set of all registered processors
     */
    Set<ComponentProcessor> processors();

    /**
     * Returns a {@link NavigableMultiMap} of all registered {@link ComponentPostProcessor}
     * instances, grouped by their priority. This does not include lazily registered processors that
     * have not yet been instantiated.
     *
     * @return a {@link NavigableMultiMap} of all registered post processors, grouped by priority
     */
    NavigableMultiMap<Integer, ComponentPostProcessor> postProcessors();

    /**
     * Returns a {@link NavigableMultiMap} of all registered {@link ComponentPreProcessor}
     * instances, grouped by their priority.
     *
     * @return a {@link NavigableMultiMap} of all registered pre processors, grouped by priority
     */
    NavigableMultiMap<Integer, ComponentPreProcessor> preProcessors();

    /**
     * Returns a set of all lazily registered {@link ComponentPostProcessor} types that have not yet
     * been instantiated.
     *
     * @return a set of all uninitialized post processor types
     */
    Set<Class<? extends ComponentPostProcessor>> uninitializedPostProcessors();
}
