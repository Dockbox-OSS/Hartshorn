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

package org.dockbox.selene.nms.packets;

import net.minecraft.network.play.server.SPacketChangeGameState;

import org.dockbox.selene.core.Weather;
import org.dockbox.selene.core.objects.keys.Keys;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.Reflect;
import org.dockbox.selene.nms.properties.NativePacketProperty;
import org.dockbox.selene.packets.ChangeGameStatePacket;

/**
 * Represents a global gamestate change packet. See <a href="https://wiki.vg/Protocol#Change_Game_State">Protocol - Change Game State</a> for more
 * details. Only supports weather gamestates.
 */
public class NMSChangeGameStatePacket extends ChangeGameStatePacket implements NMSPacket<SPacketChangeGameState> {

    private SPacketChangeGameState nativePacket;

    @Override
    public Weather getWeather() {
        int state = Reflect.getFieldValue(
            SPacketChangeGameState.class,
            this.nativePacket,
            "field_149140_b", // state
            int.class)
            .orElse(Weather.CLEAR.getGameStateId());
        return Weather.getByGameStateId(state);
    }

    @Override
    public void setWeather(Weather weather) {
        this.nativePacket = new SPacketChangeGameState(weather.getGameStateId(), 0f);
    }

    @Override
    public SPacketChangeGameState getPacket() {
        return null == this.nativePacket ? new SPacketChangeGameState(super.getWeather().getGameStateId(), 0f) : this.nativePacket;
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... injectorProperties) {
        this.nativePacket = Keys.getPropertyValue(
            NativePacketProperty.KEY,
            SPacketChangeGameState.class,
            injectorProperties
        ).orElseGet(SPacketChangeGameState::new);
    }
}
