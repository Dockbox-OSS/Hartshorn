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

import org.dockbox.hartshorn.api.config.Environment;
import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.api.exceptions.ExceptionLevels;

public class JUnitGlobalConfig implements GlobalConfig {
    @Override
    public boolean getStacktracesAllowed() {
        return false;
    }

    @Override
    public ExceptionLevels getExceptionLevel() {
        return ExceptionLevels.MINIMAL;
    }

    @Override
    public Environment getEnvironment() {
        return Environment.JUNIT;
    }

    @Override
    public String getDiscordLoggingCategoryId() {
        throw new UnsupportedOperationException("This feature is not available while running tests");
    }

    @Override
    public String getVersion() {
        return "dev";
    }
}