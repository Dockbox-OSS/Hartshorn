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
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Set;
import java.util.function.Consumer;

public abstract class SeleneModuleBootstrap extends SeleneBootstrap {

    private Set<Consumer<Object>> instanceConsumers;
    private Set<Runnable> postInitRunners;

    protected SeleneModuleBootstrap(InjectConfiguration early, InjectConfiguration late) {
        super(early, late);
        this.preparePostInitRunners();
        this.prepareInstanceConsumers();
    }

    public static SeleneModuleBootstrap instance() {
        return (SeleneModuleBootstrap) SeleneBootstrap.instance();
    }

    @Override
    protected void init() {
        super.init();
        this.initialiseModules(this.getModuleConsumer());
        this.postInitRunners.forEach(Runnable::run);
    }

    /**
     * Initiates integrated modules and performs a given consumer on each loaded module.
     *
     * @param consumer
     *         The consumer to apply
     */
    private void initialiseModules(Consumer<ModuleContainer> consumer) {
        super.getContext().get(ModuleManager.class).initialiseModules().forEach(consumer);
    }

    private Consumer<ModuleContainer> getModuleConsumer() {
        return (ModuleContainer ctx) -> ctx.instance().present(i -> {
            for (Consumer<Object> instanceConsumer : this.instanceConsumers) {
                instanceConsumer.accept(i);
            }
        });
    }

    public void registerPostInit(Runnable runnable) {
        this.preparePostInitRunners();
        this.postInitRunners.add(runnable);
    }

    public void registerInitBus(Consumer<Object> registerAction) {
        this.prepareInstanceConsumers();
        this.instanceConsumers.add(registerAction);
    }

    private void preparePostInitRunners() {
        if (null == this.postInitRunners) this.postInitRunners = SeleneUtils.emptySet();
    }
    private void prepareInstanceConsumers() {
        if (null == this.instanceConsumers) this.instanceConsumers = SeleneUtils.emptySet();
    }
}
