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

package org.dockbox.hartshorn.server.minecraft;

import org.dockbox.hartshorn.api.HartshornBootstrap;
import org.dockbox.hartshorn.api.events.EventBus;
import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerInitEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerPostInitEvent;

@Posting({ ServerInitEvent.class, ServerPostInitEvent.class })
public class MinecraftServerBootstrap extends HartshornBootstrap {

    public static MinecraftServerBootstrap instance() {
        return (MinecraftServerBootstrap) HartshornBootstrap.instance();
    }

    @Override
    public void init() {
        EventBus bus = super.context().get(EventBus.class);
        bus.post(new ServerInitEvent());
        super.init();
        bus.post(new ServerPostInitEvent());
    }
}
