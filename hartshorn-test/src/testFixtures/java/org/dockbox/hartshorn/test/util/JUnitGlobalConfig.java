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

package org.dockbox.hartshorn.test.util;

import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.api.exceptions.ExceptionLevels;

public class JUnitGlobalConfig implements GlobalConfig {

    @Override
    public boolean stacktraces() {
        return false;
    }

    @Override
    public ExceptionLevels level() {
        return ExceptionLevels.MINIMAL;
    }

    @Override
    public String discordCategory() {
        throw new UnsupportedOperationException("This feature is not available while running tests");
    }

    @Override
    public String version() {
        return "dev";
    }
}
