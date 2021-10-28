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

package org.dockbox.hartshorn.i18n.message;

import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.annotations.Resource;
import org.dockbox.hartshorn.i18n.common.Message;

@Service(owner = Hartshorn.class)
public interface DefaultResources {

    static DefaultResources instance(final ApplicationContext context) {
        return context.get(DefaultResources.class);
    }

    @Resource(value = "$3[] $1", key = "prefix")
    Message prefix();

    @Resource(value = "$4An unknown occurred. $3{0}", key = "caught")
    Message unknownError(String message);

    @Resource(value = "$4Reference to object lost", key = "caught.reference.lost")
    Message referenceLost();

    @Resource(value = "Unknown", key = "source.unknown")
    Message unknown();

    @Resource(value = "$4{0}", key = "hartshorn.exception")
    Message exception(String message);

}
