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

package org.dockbox.selene.sample;

import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.packets.data.Weather;
import org.dockbox.selene.plots.flags.AbstractPlotFlag;

public class WeatherFlag extends AbstractPlotFlag<Weather> {

    public WeatherFlag(String id, ResourceEntry description) {
        super(id, description);
    }

    @Override
    public String serialize(Weather object) {
        return String.valueOf(object.getGameStateId());
    }

    @Override
    public Weather parse(String raw) {
        int gameState = Integer.parseInt(raw);
        return Weather.getByGameStateId(gameState);
    }

    @Override
    public Class<Weather> getType() {
        return Weather.class;
    }
}
