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

package org.dockbox.selene.api.objects.keys.data;

import org.dockbox.selene.api.module.ModuleContainer;
import org.dockbox.selene.api.objects.keys.Keys;
import org.dockbox.selene.api.util.Reflect;

public final class IntegerPersistentDataKey extends TypedPersistentDataKey<Integer> {

    private IntegerPersistentDataKey(String name, String id, ModuleContainer module) {
        super(name, id, module, Integer.class);
    }

    public static IntegerPersistentDataKey of(String name, Class<?> owningClass) {
        ModuleContainer module = Reflect.getModule(owningClass);
        return of(name, module);
    }

    public static IntegerPersistentDataKey of(String name, ModuleContainer module) {
        String id = Keys.convertToModuleIdString(name, module);
        return new IntegerPersistentDataKey(name, id, module);
    }
}
