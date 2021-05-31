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

package org.dockbox.selene.server.minecraft;

import org.dockbox.selene.api.SeleneBootstrap;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.server.minecraft.events.server.ServerInitEvent;

public class MinecraftServerBootstrap extends SeleneBootstrap {

    public static MinecraftServerBootstrap instance() {
        return (MinecraftServerBootstrap) SeleneBootstrap.instance();
    }

    @Override
    public void init() {
        super.init();
        EventBus bus = super.getContext().get(EventBus.class);
        bus.post(new ServerInitEvent());
    }
}
