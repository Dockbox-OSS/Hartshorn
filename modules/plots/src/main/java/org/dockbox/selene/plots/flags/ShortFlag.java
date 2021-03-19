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

package org.dockbox.selene.plots.flags;

import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.server.Selene;

public class ShortFlag extends AbstractPlotFlag<Short> {

    protected ShortFlag(String id, ResourceEntry description) {
        super(id, description);
    }

    @Override
    public String serialize(Short object) {
        return String.valueOf(object);
    }

    @Override
    public Short parse(String raw) {
        try {
            return Short.parseShort(raw);
        } catch (NumberFormatException e) {
            Selene.handle("Could not parse number flag: " + getId(), e);
            return -1;
        }
    }

    @Override
    public Class<Short> getType() {
        return Short.class;
    }
}
