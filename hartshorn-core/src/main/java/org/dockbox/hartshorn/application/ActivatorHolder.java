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

package org.dockbox.hartshorn.application;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.dockbox.hartshorn.util.option.Option;

/**
 * A holder for {@link org.dockbox.hartshorn.component.processing.ServiceActivator} meta-{@link Annotation}s
 * that may be used to activate components, services, or configurations.
 *
 * @since 0.4.11
 *
 * @see org.dockbox.hartshorn.component.processing.ServiceActivator
 * @see org.dockbox.hartshorn.component.condition.RequiresActivator
 *
 * @author Guus Lieben
 */
public interface ActivatorHolder {

    /**
     * Returns all {@link Annotation}s that are present on this holder.
     *
     * @return all annotations present on this holder
     */
    Set<Annotation> activators();

    /**
     * Returns whether this holder has an {@link Annotation} of the provided type.
     *
     * @param activator the type of annotation to check for
     * @return {@code true} if the holder has an annotation of the provided type, {@code false} otherwise
     */
    boolean hasActivator(Class<? extends Annotation> activator);

    /**
     * Returns the {@link Annotation} of the provided type, if present.
     *
     * @param activator the type of annotation to return
     * @return the annotation of the provided type, if present
     * @param <A> the type of annotation to return
     */
    <A> Option<A> activator(Class<A> activator);
}
