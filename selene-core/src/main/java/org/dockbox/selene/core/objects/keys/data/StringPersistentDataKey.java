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

package org.dockbox.selene.core.objects.keys.data;

import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.objects.keys.Keys;
import org.dockbox.selene.core.util.SeleneUtils;

public final class StringPersistentDataKey extends TypedPersistentDataKey<String> {

    private StringPersistentDataKey(String name, String id, Extension extension) {
        super(name, id, extension, String.class);
    }

    public static StringPersistentDataKey of(String name, Class<?> owningClass) {
        Extension extension = SeleneUtils.REFLECTION.getExtension(owningClass);
        return of(name, extension);
    }

    public static StringPersistentDataKey of(String name, Extension extension) {
        String id = Keys.convertToExtensionIdString(name, extension);
        return new StringPersistentDataKey(name, id, extension);
    }
}
