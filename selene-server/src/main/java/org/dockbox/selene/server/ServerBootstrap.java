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
import org.dockbox.selene.di.binding.BindingData;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.server.events.ServerStartedEvent;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ServerBootstrap extends SeleneModuleBootstrap {

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link
     * InjectConfiguration}. Also verifies dependency artifacts and injector bindings. Proceeds
     * to {@link SeleneBootstrap#construct()} once verified.
     *
     * @param early
     *         the injector provided by the Selene implementation to create in pre-construct phase
     * @param late
     *         the injector provided by the Selene implementation to create in construct phase
     */
    protected ServerBootstrap(InjectConfiguration early, InjectConfiguration late) {
        super(early, late);
    }

    /**
     * Prints information about registered instances. This includes injection bindings, modules, and
     * event handlers. This method is typically only used when starting the server.
     *
     * @param event
     *         The server event indicating the server started
     */
    @Listener
    public void debugRegisteredInstances(ServerStartedEvent event) {
        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded bindings: ");
        AtomicInteger unprovisionedTypes = new AtomicInteger();

        for (BindingData binding : this.getInjector().getBindingData()) {
            String meta = binding.getMeta().present() ? " (meta: " + binding.getMeta().get().value() + ")" : "";
            Selene.log().info("  - \u00A77" + binding.getSource().getSimpleName() + meta + ": \u00A78" + binding.getTarget().getSimpleName());
        }

        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded modules: ");
        ModuleManager em = Provider.provide(ModuleManager.class);
        em.getRegisteredModuleIds().forEach(ext -> {
            Exceptional<ModuleContainer> header = em.getContainer(ext);
            if (header.present()) {
                ModuleContainer ex = header.get();
                String module = "  - \u00A77" + ex.name() + " \u00A78(" + ex.id() + ")";
                Selene.log().info(module);
            }
            else {
                Selene.log().info("  - \u00A77" + ext + " \u00A78(Missing header)");
            }
        });

        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded event handlers: ");
        Provider.provide(EventBus.class).getListenersToInvokers().forEach((listener, invokers) -> {
            Class<?> type;
            if (listener instanceof Class) type = (Class<?>) listener;
            else type = listener.getClass();

            String entry = "  - \u00A77" + type.getSimpleName();

            String[] objects = invokers.stream().map(invoker -> "\u00A77" + invoker.getEventType().getSimpleName()).toArray(String[]::new);
            String events = String.join(", ", objects);
            Selene.log().info(entry + " (" + events + "\u00A77)");
        });
    }

}
