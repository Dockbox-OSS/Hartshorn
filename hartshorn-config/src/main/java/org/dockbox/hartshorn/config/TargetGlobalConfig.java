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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.api.config.Environment;
import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.api.exceptions.ExceptionLevels;
import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.Value;

import lombok.Getter;

@Getter
@Configuration("hartshorn")
public class TargetGlobalConfig implements GlobalConfig {

    @Value(value = "hartshorn.exceptions.stacktraces", or = "false")
    private boolean stacktracesAllowed;

    @Value(value = "hartshorn.exceptions.level", or = "NATIVE")
    private ExceptionLevels exceptionLevel;

    @Value(value = "hartshorn.environment", or = "DEVELOPMENT")
    private Environment environment;

    @Value("hartshorn.discord.logging-channel")
    private String discordLoggingCategoryId;

    @Value("hartshorn.version")
    private String version;

    @Override
    public boolean getStacktracesAllowed() {
        return this.stacktracesAllowed;
    }
}