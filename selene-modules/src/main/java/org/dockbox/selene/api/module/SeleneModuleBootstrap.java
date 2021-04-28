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

package org.dockbox.selene.api.module;

import org.dockbox.selene.api.SeleneBootstrap;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Set;
import java.util.function.Consumer;

public abstract class SeleneModuleBootstrap extends SeleneBootstrap {

    private final Set<Consumer<Object>> instanceConsumers = SeleneUtils.emptySet();
    private final Set<Runnable> postInitRunners = SeleneUtils.emptySet();

    protected SeleneModuleBootstrap(InjectConfiguration early, InjectConfiguration late) {
        super(early, late);
    }

    public static SeleneModuleBootstrap getInstance() {
        return (SeleneModuleBootstrap) SeleneBootstrap.getInstance();
    }

    @Override
    protected void init() {
        super.init();
        SeleneModuleBootstrap.initialiseModules(this.getModuleConsumer());
        this.postInitRunners.forEach(Runnable::run);
    }

    /**
     * Initiates integrated modules and performs a given consumer on each loaded module.
     *
     * @param consumer
     *         The consumer to apply
     */
    private static void initialiseModules(Consumer<ModuleContainer> consumer) {
        Provider.provide(ModuleManager.class).initialiseModules().forEach(consumer);
    }

    private Consumer<ModuleContainer> getModuleConsumer() {
        return (ModuleContainer ctx) -> {
            Class<?> type = ctx.type();
            Exceptional<?> oi = Exceptional.of(super.getInstance(type));

            oi.present(i -> {
                for (Consumer<Object> instanceConsumer : this.instanceConsumers) {
                    //noinspection unchecked
                    super.getInjector().bind((Class<? super Object>) type, i);
                    instanceConsumer.accept(i);
                }
            });
        };
    }

    public void registerPostInit(Runnable runnable) {
        this.postInitRunners.add(runnable);
    }

    public void registerInitBus(Consumer<Object> registerAction) {
        this.instanceConsumers.add(registerAction);
    }
}
