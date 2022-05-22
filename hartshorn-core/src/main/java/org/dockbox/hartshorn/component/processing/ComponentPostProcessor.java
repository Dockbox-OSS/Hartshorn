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

package org.dockbox.hartshorn.component.processing;

import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.application.context.ApplicationContext;

import java.lang.annotation.Annotation;

/**
 * A component post processor is responsible for processing a component after it has been created. This
 * can be used to add additional functionality to a component, or to modify the component in some way.
 *
 * <p>The component post processor will only process a component if it is known to the application, which
 * is validated through the active {@link ComponentLocator}. If no {@link ComponentContainer} exists for
 * the component, the component post processor will not be called.
 *
 * <p>To specify when a component post processor should be active, and when the application should ignore
 * it, the processor will always require an activator annotation to be specified as type parameter
 * <code>A</code>. If the specified annotation is not annotated with {@link ServiceActivator}, it is up to
 * the active {@link ApplicationContext} to decide whether this should be considered an error or not.
 *
 * <p>The component post processor will be called for each component that is created, and will be called
 * in the order of the specified {@link OrderedComponentProcessor#order()} value.
 *
 * @param <A> The type of the activator annotation.
 * @author Guus Lieben
 * @since 22.1
 */
public non-sealed interface ComponentPostProcessor<A extends Annotation> extends ComponentProcessor<A> {
}
