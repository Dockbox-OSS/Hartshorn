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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.i18n.annotations.Resource;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;

@Service(owner = CommandGateway.class)
public interface CommandParameterResources {

    @Resource(value = "Not enough arguments.", key = "missing_args")
    ResourceEntry notEnoughArgs();

    @Resource(value = "Pattern has to be formatted as #type[arg1][arg2][etc.]", key = "hashtag.wrong_format")
    ResourceEntry wrongHashtagPatternFormat();

    @Resource(value = "Parameter of type {0} has no register converter", key = "missing_converter")
    ResourceEntry missingConverter(String type);

}
