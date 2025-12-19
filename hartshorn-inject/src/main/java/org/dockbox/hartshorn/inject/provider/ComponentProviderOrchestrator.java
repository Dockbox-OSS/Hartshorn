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

package org.dockbox.hartshorn.inject.provider;

import org.dockbox.hartshorn.inject.processing.HierarchicalBinderProcessorRegistry;
import org.dockbox.hartshorn.inject.scope.Scope;

/**
 * Orchestrator for component providers, which allows for the management of component providers
 * within various scopes. Alongside additional scopes, there is always a global scope available, for
 * which the provider is exposed through {@link #applicationProvider()}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ComponentProviderOrchestrator extends PostProcessingComponentProvider {

    /**
     * Returns the registry for binder processors. The processors in this registry are used to
     * process default bindings on all component providers in the orchestrator.
     *
     * @return the binder processor registry
     */
    HierarchicalBinderProcessorRegistry binderProcessorRegistry();

    /**
     * Returns the application provider, which is the default provider for the global scope.
     *
     * @return the application provider
     */
    HierarchicalComponentProvider applicationProvider();

    /**
     * Returns whether a component provider exists for the specified scope.
     *
     * @param scope the scope for which to retrieve the provider
     *
     * @return {@code true} if a provider exists for the specified scope, {@code false} otherwise
     */
    boolean containsScope(Scope scope);
}
