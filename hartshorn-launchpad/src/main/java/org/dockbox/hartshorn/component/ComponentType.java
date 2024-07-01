/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.context.ContextView;

/**
 * Represents the type of a component, typically represented through {@link Component#type()} or
 * {@link ComponentContainer#componentType()}.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 *
 * @deprecated Component stereotypes and metadata should be used instead of this enum.
 */
@Deprecated(since = "0.6.0", forRemoval = true)
public enum ComponentType {
    /**
     * Components range from POJO's to {@link ContextView context} types. The common property of
     * all components is that they lack functionality, and only provide context. In domain objects the provided context
     * is the entity definition, in persistent entities the context is the data represented by the entity, and in the
     * case of the {@link ApplicationContext} it is information about all application
     * components.
     *
     * <p>Components do not require explicit identification, but can be annotated with {@link Component}. When annotated
     * with {@link Component}, the component will be registered to the active
     * {@link ApplicationContext}.
     */
    INJECTABLE,

    /**
     * Like regular components, functional components will always allow injection. Functional components are more
     * commonly named services. Unlike regular components, services do not define context, and instead act based on
     * provided context. They can provide a wide range of functionality based on specific contexts. Services should
     * always be identified with {@link Service}, or an extension of it.
     *
     * <p>Functional components can be processed by {@link ComponentPreProcessor}s and
     * be modified by {@link ComponentPostProcessor}s. In the case of services, the
     * {@link ComponentPreProcessor} and
     * {@link ComponentPostProcessor} directly target service definitions.
     *
     * <p>As services are able to provide functionality based on a given context, all services are singletons by
     * default. This allows you to better optimize your application, and to ensure a state can safely be stored inside
     * services.
     */
    FUNCTIONAL,
}
