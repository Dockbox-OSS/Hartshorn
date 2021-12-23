/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.services;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.annotations.activate.ServiceActivator;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.annotation.Annotation;

/**
 * A component pre-processor is responsible for pre-processing a component. This can be used to
 * validate the component before it is added to the application context, or to add additional
 * information to the component before it is created.
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
 * <p>The component pre-processor will be called for each component that is added to the application.
 * It is executed during application construction.
 *
 * @author Guus Lieben
 * @since 4.2.5
 * @param <A>
 */
public interface ComponentPreProcessor<A extends Annotation> extends ComponentProcessor<A> {

    @Override
    default <T> boolean modifies(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        return this.modifies(context, type);
    }

    @Override
    default <T> T process(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        this.process(context, type);
        return instance;
    }

    /**
     * Determines whether the component pre-processor modifies the component. As component instances will
     * not exist yet, this method does not expect the <code>instance</code> to be specified.
     *
     * @param context The application context.
     * @param type The type context of the component.
     * @return <code>true</code> if the component pre-processor modifies the component, <code>false</code>
     * otherwise.
     * @see ComponentProcessor#modifies(ApplicationContext, TypeContext, Object)
     * @see ComponentProcessor#preconditions(ApplicationContext, TypeContext, Object)
     */
    boolean modifies(ApplicationContext context, TypeContext<?> type);

    /**
     * Processes a given component. As component instances will not exist yet, this method does not expect
     * the <code>instance</code> to be specified.
     *
     * @param context The application context.
     * @param type The type context of the component.
     * @param <T> The type of the component.
     * @see ComponentProcessor#process(ApplicationContext, TypeContext, Object)
     */
    <T> void process(ApplicationContext context, TypeContext<T> type);
}
