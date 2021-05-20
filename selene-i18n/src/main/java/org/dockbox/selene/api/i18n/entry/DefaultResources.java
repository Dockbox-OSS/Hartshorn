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

package org.dockbox.selene.api.i18n.entry;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.i18n.annotations.Resource;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.di.annotations.Service;

@Service(owner = Selene.class)
public interface DefaultResources {

    @Resource(value = "$3[] $1", key = "prefix")
    ResourceEntry getPrefix();

    @Resource(value = "$4An caught occurred. $3{0}", key = "caught")
    ResourceEntry getUnknownError(String message);

    @Resource(value = "$4Cannot apply to this type", key = "caught.failedbinding")
    ResourceEntry getBindingFailure();

    @Resource(value = "$4Reference to object lost", key = "caught.reference.lost")
    ResourceEntry getReferenceLost();

    @Resource(value = "Unknown", key = "source.unknown")
    ResourceEntry getUnknown();

    @Resource(value = "None", key = "source.none")
    ResourceEntry getNone();

    @Resource(value = "$4{0}", key = "selene.exception")
    ResourceEntry getException(String message);

    static DefaultResources instance() {
        return Selene.context().get(DefaultResources.class);
    }

}
