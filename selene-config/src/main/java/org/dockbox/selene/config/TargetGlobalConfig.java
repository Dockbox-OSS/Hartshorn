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

package org.dockbox.selene.config;

import org.dockbox.selene.api.config.Environment;
import org.dockbox.selene.api.config.GlobalConfig;
import org.dockbox.selene.api.exceptions.ExceptionLevels;
import org.dockbox.selene.config.annotations.Value;

public class TargetGlobalConfig implements GlobalConfig {

    @Value(value = "selene.exceptions.stacktraces", or = "false")
    private boolean stacktraces;

    @Value(value = "selene.exceptions.level", or = "NATIVE")
    private ExceptionLevels level;

    @Value(value = "selene.environment", or = "DEVELOPMENT")
    private Environment environment;

    @Value("selene.discord.logging-channel")
    private String loggingCategoryId;

    @Override
    public boolean getStacktracesAllowed() {
        return this.stacktraces;
    }

    @Override
    public ExceptionLevels getExceptionLevel() {
        return this.level;
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public String getDiscordLoggingCategoryId() {
        return this.loggingCategoryId;
    }
}
