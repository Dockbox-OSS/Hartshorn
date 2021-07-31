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

import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.api.exceptions.ExceptionLevels;
import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.Value;

import lombok.Getter;

/**
 * Simple implementation of {@link GlobalConfig} using {@link Value} based
 * field population.
 */
@Getter
@Configuration
public class TargetGlobalConfig implements GlobalConfig {

    @Value(value = "hartshorn.exceptions.stacktraces", or = "true")
    private boolean stacktraces;

    @Value(value = "hartshorn.exceptions.level", or = "FRIENDLY")
    private ExceptionLevels level;

    @Value("hartshorn.version")
    private String version;

}
