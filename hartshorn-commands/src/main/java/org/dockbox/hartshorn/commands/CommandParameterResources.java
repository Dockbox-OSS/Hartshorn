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

import org.dockbox.hartshorn.api.i18n.annotations.Resource;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.commands.service.CommandParameters;
import org.dockbox.hartshorn.di.annotations.service.Service;

@Service(owner = CommandParameters.class)
public interface CommandParameterResources {

    @Resource(value = "Not enough arguments.", key = "missing_args")
    ResourceEntry getNotEnoughArgs();

    @Resource(value = "Pattern has to be formatted as #type[arg1][arg2][etc.]", key = "hashtag.wrong_format")
    ResourceEntry getWrongHashtagPatternFormat();

    @Resource(value = "Parameter of type {0} has no register converter", key = "missing_converter")
    ResourceEntry getMissingConverter(String type);

}
