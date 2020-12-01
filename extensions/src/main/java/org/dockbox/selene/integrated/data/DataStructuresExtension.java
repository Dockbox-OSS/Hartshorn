/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.integrated.data;

import com.google.common.reflect.TypeToken;

import org.dockbox.selene.integrated.data.registry.Registry;
import org.dockbox.selene.core.impl.files.serialize.PredicateSerializerInformation;
import org.dockbox.selene.integrated.data.serializers.RegistrySerializer;
import org.dockbox.selene.core.annotations.extension.Extension;

@Extension(id = "datastructures", name = "Additional data structures",
           description = "Provides additional data structures for specific usages",
           authors = {"GuusLieben", "pumbas600"}, uniqueId = "5755725b-e24b-4ca6-9944-56967ac41949")
public class DataStructuresExtension {

    public DataStructuresExtension() {
        new PredicateSerializerInformation<>(Registry.class, RegistrySerializer::new, new TypeToken<Registry<?>>() {
        });
    }

}
