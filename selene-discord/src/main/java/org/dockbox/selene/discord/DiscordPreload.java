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

package org.dockbox.selene.discord;

import org.dockbox.selene.di.Preloadable;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.util.Reflect;

import java.util.function.Consumer;

class DiscordPreload implements Preloadable {
    @Override
    public void preload() {
        Class<?> moduleBootstrap = Reflect.lookup("org.dockbox.selene.api.value.SeleneModuleBootstrap");
        if (moduleBootstrap != null) {
            DiscordUtils du = Provider.provide(DiscordUtils.class);
            Reflect.runMethod(moduleBootstrap, null, "getInstance", moduleBootstrap).present(bootstrap -> {
                Reflect.runMethod(bootstrap, "registerInitBus", null, (Consumer<Object>) du::registerCommandListener);
            });
        }
    }
}
