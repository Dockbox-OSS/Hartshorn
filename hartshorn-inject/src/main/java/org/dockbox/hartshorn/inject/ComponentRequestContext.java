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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.targets.InjectionPoint;

/**
 * A context that is used to provide information about the current component request. This gives additional
 * insights of <i>where</i> a component will be used, contrary to a {@link ComponentKey} which describes the
 * component itself.
 *
 * <p>The target of the request context is typically an injection point, but it may also be used to describe
 * the use case of a component in other scenarios through custom context implementations.
 *
 * @see InjectionPoint
 * @see ComponentKey
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public final class ComponentRequestContext extends DefaultContext {

    private final InjectionPoint injectionPoint;

    private ComponentRequestContext(InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
    }

    /**
     * Creates a new {@link ComponentRequestContext} for the given injection point.
     *
     * @param injectionPoint The injection point for which the request context is created.
     * @return A new request context for the given injection point.
     */
    public static ComponentRequestContext createForInjectionPoint(InjectionPoint injectionPoint) {
        return new ComponentRequestContext(injectionPoint);
    }

    /**
     * Creates a new {@link ComponentRequestContext} for a component that is not used in a specific injection
     * point, or for a component that is used in an unknown context.
     *
     * @return A new request context for a component.
     */
    public static ComponentRequestContext createForComponent() {
        return new ComponentRequestContext(null);
    }

    /**
     * Returns the injection point for which the request context was created. If the request context was created
     * for a component that is not used in a specific injection point, this method will return {@code null}.
     *
     * @return The injection point for which the request context was created, or {@code null} if the request context
     * was created for a component.
     */
    public InjectionPoint injectionPoint() {
        return this.injectionPoint;
    }

    /**
     * Returns whether the request context was created for a specific injection point.
     *
     * @return {@code true} if the request context was created for a specific injection point, {@code false} otherwise.
     */
    public boolean isForInjectionPoint() {
        return this.injectionPoint != null;
    }
}
