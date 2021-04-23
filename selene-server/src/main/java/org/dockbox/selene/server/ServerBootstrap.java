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

package org.dockbox.selene.server;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneBootstrap;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.events.annotations.Listener;
import org.dockbox.selene.api.module.ModuleContainer;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.module.SeleneModuleBootstrap;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.server.events.ServerStartedEvent;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ServerBootstrap extends SeleneModuleBootstrap {

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link
     * InjectConfiguration}. Also verifies dependency artifacts and injector bindings. Proceeds
     * to {@link SeleneBootstrap#construct()} once verified.
     *
     * @param moduleConfiguration
     *         the injector provided by the Selene implementation
     */
    protected ServerBootstrap(InjectConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    /**
     * Prints information about registered instances. This includes injection bindings, modules, and
     * event handlers. This method is typically only used when starting the server.
     *
     * @param event
     *         The server event indicating the server started
     */
    @Listener
    protected void debugRegisteredInstances(ServerStartedEvent event) {
        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded bindings: ");
        AtomicInteger unprovisionedTypes = new AtomicInteger();
        // TODO: Migrate to native
//        super.getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
//            try {
//                Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
//                Class<?> providerType = binding.getProvider().get().getClass();
//
//                if (!keyType.equals(providerType) && null != providerType)
//                    Selene.log().info("  - \u00A77" + keyType.getSimpleName() + ": \u00A78" + providerType.getCanonicalName());
//            }
//            catch (ProvisionException | AssertionError e) {
//                unprovisionedTypes.getAndIncrement();
//            }
//        });
//        Selene.log().info("  \u00A77.. and " + unprovisionedTypes.get() + " unprovisioned types.");

        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded modules: ");
        ModuleManager em = Provider.provide(ModuleManager.class);
        em.getRegisteredModuleIds().forEach(ext -> {
            Exceptional<ModuleContainer> header = em.getContainer(ext);
            if (header.present()) {
                ModuleContainer ex = header.get();
                Selene.log().info("  - \u00A77" + ex.name());
                Selene.log().info("  | - \u00A77ID: \u00A78" + ex.id());
                Selene.log().info("  | - \u00A77Authors: \u00A78" + Arrays.toString(ex.authors()));
                Selene.log().info("  | - \u00A77Dependencies: \u00A78" + Arrays.toString(ex.dependencies()));
            }
            else {
                Selene.log().info("  - \u00A77" + ext + " \u00A78(No header)");
            }
        });

        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded event handlers: ");
        Provider.provide(EventBus.class).getListenersToInvokers().forEach((listener, invokers) -> {
            Class<?> type;
            if (listener instanceof Class) type = (Class<?>) listener;
            else type = listener.getClass();

            Selene.log().info("  - \u00A77" + type.getCanonicalName());
            invokers.forEach(invoker ->
                    Selene.log().info("  | - \u00A77" + invoker.getEventType().getSimpleName() + ": \u00A78" + invoker.getMethod().getName()));
        });
    }

}
