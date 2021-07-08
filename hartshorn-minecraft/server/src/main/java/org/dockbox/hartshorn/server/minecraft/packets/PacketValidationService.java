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

package org.dockbox.hartshorn.server.minecraft.packets;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.events.EventBus;
import org.dockbox.hartshorn.api.events.parents.Event;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.server.minecraft.events.packet.PacketEvent;
import org.dockbox.hartshorn.server.minecraft.packets.annotations.Packet;
import org.dockbox.hartshorn.util.Reflect;

@Service(activators = UseBootstrap.class)
class PacketValidationService {

    @PostBootstrap
    public void addEventValidation() {
        EventBus bus = Hartshorn.context().get(EventBus.class);
        bus.addValidationRule(method -> {
            for (Class<?> param : method.getParameterTypes()) {
                if (Reflect.assigns(Event.class, param)) {
                    if (Reflect.assigns(PacketEvent.class, param)
                            && !method.isAnnotationPresent(Packet.class)) {
                        return Exceptional.of(false, new IllegalArgumentException("Needs @Packet annotation: " + method.toGenericString()));
                    }
                    return Exceptional.of(true);
                }
            }
            // Typically already caught by the event bus itself, this is just so we return a validated message
            return Exceptional.of(false, new IllegalArgumentException("At least one parameter should be a subclass of Event: " + method.toGenericString()));
        });
    }
}
