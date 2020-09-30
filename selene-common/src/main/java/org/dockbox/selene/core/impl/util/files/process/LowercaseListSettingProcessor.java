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

/*
 * This file is part of Neutrino, licensed under the MIT License (MIT).
 */
package org.dockbox.selene.core.impl.util.files.process;

import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.stream.Collectors;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class LowercaseListSettingProcessor implements SettingProcessor {

    private final TypeToken<List<String>> ttListString = new TypeToken<List<String>>(String.class) {
    };
    private final TypeToken<String> ttString = TypeToken.of(String.class);

    @Override
    public void process(ConfigurationNode cn) throws ObjectMappingException {
        if (cn.isVirtual()) {
            return;
        }

        cn.setValue(this.ttListString, cn.getList(this.ttString)
                .stream()
                .map(x -> x.toLowerCase().replace(" ", "_"))
                .collect(Collectors.toList())
        );
    }
}
