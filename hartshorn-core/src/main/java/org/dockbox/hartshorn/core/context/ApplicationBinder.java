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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.binding.BindingHierarchy;
import org.dockbox.hartshorn.core.inject.Binder;
import org.dockbox.hartshorn.core.inject.ProviderContext;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;
import org.dockbox.hartshorn.core.services.ComponentPreProcessor;

/**
 * A specialized {@link Binder} that is used to bind prefixes and {@link InjectConfiguration}s. These configurations
 * are typically used to configure an active {@link ApplicationContext}.
 */
public interface ApplicationBinder extends Binder {

    /**
     * Binds the given {@link InjectConfiguration}, which typically contains only static bindings.
     *
     * @param configuration The configuration to bind.
     */
    void bind(InjectConfiguration configuration);

    /**
     * Binds the given prefix, which represents a package. The given prefix is scanned for  {@link ComponentPreProcessor}s
     * and {@link ComponentPostProcessor}s, as well as automatically bound types through {@link Binds} and its combined
     * annotation {@link org.dockbox.hartshorn.core.annotations.inject.Combines}. The prefix is also registered to the
     * active {@link ApplicationEnvironment}. After all components are registered, the located
     * {@link ComponentPreProcessor}s are activated.
     *
     * @param prefix The prefix to bind.
     */
    void bind(String prefix);

    /**
     * Binds the given {@link ProviderContext} to the {@link Key} provided through {@link ProviderContext#key()}. The
     * context is directly registered to the {@link BindingHierarchy} of the {@link Key}, using its
     * {@link ProviderContext#priority()}. If the context is marked as a singleton provider through
     * {@link ProviderContext#singleton()}, the {@link ProviderContext#provider()} is immediately called to obtain a
     * singleton instance of type <code>T</code>.
     *
     * @param <T> The type of the provider context.
     * @param context The context to bind.
     */
    <T> void add(ProviderContext<T> context);
}
