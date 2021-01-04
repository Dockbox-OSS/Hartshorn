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

package org.dockbox.selene.core;


import org.dockbox.selene.core.objects.item.storage.Minecraft112Items;
import org.dockbox.selene.core.objects.item.storage.Minecraft116Items;
import org.dockbox.selene.core.objects.item.storage.MinecraftItems;

/**
 * Enum type providing the supported Minecraft versions for Selene. Typically this is used when defining vanilla
 * constants, indicating in which version they were made available.
 */
public enum MinecraftVersion {
    MC1_12("1.12", new Minecraft112Items()),
    MC1_16("1.16", new Minecraft116Items()),
    INDEV("Indev", new Minecraft112Items());

    private final String readableVersionString;
    private final MinecraftItems minecraftItems;

    MinecraftVersion(String readableVersionString, MinecraftItems minecraftItems) {
        this.readableVersionString = readableVersionString;
        this.minecraftItems = minecraftItems;
    }

    public String getReadableVersionString() {
        return this.readableVersionString;
    }

    public MinecraftItems getItems() {
        return this.minecraftItems;
    }
}
