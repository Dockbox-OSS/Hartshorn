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

import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.objects.keys.Keys;
import org.dockbox.selene.api.util.Reflect;

public final class StringPersistentDataKey extends TypedPersistentDataKey<String>
{

    private StringPersistentDataKey(String name, String id, Module module)
    {
        super(name, id, module, String.class);
    }

    public static StringPersistentDataKey of(String name, Class<?> owningClass)
    {
        Module module = Reflect.getModule(owningClass);
        return of(name, module);
    }

    public static StringPersistentDataKey of(String name, Module module)
    {
        String id = Keys.convertToModuleIdString(name, module);
        return new StringPersistentDataKey(name, id, module);
    }
}
