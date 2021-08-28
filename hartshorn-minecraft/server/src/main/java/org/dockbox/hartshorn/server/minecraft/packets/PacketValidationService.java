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

import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.events.EventBus;
import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.server.minecraft.events.packet.PacketEvent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

@Service(activators = UseBootstrap.class)
class PacketValidationService {

    @PostBootstrap
    public void addEventValidation(final ApplicationContext context) {
        final EventBus bus = context.get(EventBus.class);
        final PacketContext packetContext = new PacketContext();

        for (final EventWrapper wrapper : bus.invokers().values().stream().flatMap(Collection::stream).toList()) {

            final MethodContext<?, ?> method = wrapper.method();
            final TypeContext<?> parameterType = method.parameterTypes().get(0);
            if (parameterType.childOf(PacketEvent.class)) {

                final Type genericParameterType = method.method().getGenericParameterTypes()[0];
                if (genericParameterType instanceof ParameterizedType parameterizedType) {
                    final Type actualType = parameterizedType.getActualTypeArguments()[0];
                    if (actualType instanceof Class && TypeContext.of((Class<?>) actualType).childOf(Packet.class)) {
                        //noinspection unchecked
                        packetContext.add((Class<? extends Packet>) actualType);
                    }
                }
            }

        }
        context.add(packetContext);
    }

}
