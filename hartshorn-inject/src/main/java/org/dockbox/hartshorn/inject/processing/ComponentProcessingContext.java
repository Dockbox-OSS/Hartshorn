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

import org.dockbox.hartshorn.context.DataContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A context that is used during the processing of a component. It provides access to the
 * responsible application, and exposes information about the component being processed.
 *
 * @param <T> the type of the component being processed
 *
 * @author Guus Lieben
 * @since 0.4.10
 */
public class ComponentProcessingContext<T> extends DataContext {

    private final InjectionCapableApplication application;
    private final ComponentRequestContext requestContext;
    private final boolean permitsProxying;
    private final ComponentKey<T> key;

    protected ObjectContainer<T> container;

    public ComponentProcessingContext(
        InjectionCapableApplication application,
        ComponentRequestContext requestContext,
        ComponentKey<T> key,
        ObjectContainer<T> container,
        boolean permitsProxying
    ) {
        this.application = application;
        this.requestContext = requestContext;
        this.key = key;
        this.container = container;
        this.permitsProxying = permitsProxying;
    }

    /**
     * Returns the application responsible for this processing context.
     *
     * @return the application
     */
    public InjectionCapableApplication application() {
        return this.application;
    }

    /**
     * Returns the request context which was used to request the component being processed.
     *
     * @return the request context
     */
    public ComponentRequestContext requestContext() {
        return this.requestContext;
    }

    /**
     * Returns the key of the component being processed.
     *
     * @return the component key
     */
    public ComponentKey<T> key() {
        return this.key;
    }

    /**
     * Returns the instance of the component being processed, if available.
     *
     * @return the component instance, or {@code null} if not available
     */
    public T instance() {
        return this.container.instance();
    }

    /**
     * Returns the container that holds the component being processed, if available.
     *
     * @return the object container for the component, or {@code null} if not available
     */
    public ObjectContainer<T> container() {
        return this.container;
    }

    /**
     * Returns whether the component being processed permits proxying.
     *
     * @return {@code true} if proxying is permitted, {@code false} otherwise
     */
    public boolean permitsProxying() {
        return this.permitsProxying;
    }

    /**
     * Returns the type view of the component being processed.
     *
     * @return the type view of the component
     */
    public TypeView<T> type() {
        if (this.container != null) {
            T instance = this.container.instance();
            if (instance != null) {
                return this.application.environment().introspector().introspect(instance);
            }
        }
        return this.application.environment().introspector().introspect(this.key.type());
    }
}
