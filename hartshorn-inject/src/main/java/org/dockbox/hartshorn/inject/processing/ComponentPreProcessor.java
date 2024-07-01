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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.InjectorContext;

/**
 * A component pre-processor is responsible for pre-processing a component. This can be used to
 * validate the component before it is added to the application context, or to add additional
 * information to the component before it is created.
 *
 * <p>The component pre-processor will be called for each component that is added to the application.
 * It is executed during application construction.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public abstract non-sealed class ComponentPreProcessor implements ComponentProcessor {

    @Override
    public <T> T process(ComponentProcessingContext<T> processingContext) {
        this.process(processingContext.injectorContext(), processingContext);
        return processingContext.instance();
    }

    /**
     * Processes a given component. As component instances will not exist yet, this method does not expect
     * the {@code instance} to be specified.
     *
     * @param context The injector context.
     * @param processingContext The type context of the component.
     * @param <T> The type of the component.
     * @see ComponentProcessor#process(ComponentProcessingContext)
     */
    public abstract <T> void process(InjectorContext context, ComponentProcessingContext<T> processingContext);
}
