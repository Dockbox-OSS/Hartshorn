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

package org.dockbox.selene.core.objects.bossbar;

/**
 * Represents the different styles of bossbars. See <a href="https://www.spigotmc.org/attachments/2016-03-15_17-03-47-png.113003/">this image</a>
 * for reference regarding the visual representation of these styles.
 */
public enum BossbarStyle
{

    /**
     * A bossbar split into 10 parts, separated by vertical notches.
     */
    NOTCHED_10,

    /**
     * A bossbar split into 12 parts, separated by vertical notches.
     */
    NOTCHED_12,
    /**
     * A bossbar split into 20 parts, separated by vertical notches.
     */
    NOTCHED_20,
    /**
     * A bossbar split into 6 parts, separated by vertical notches.
     */
    NOTCHED_6,
    /**
     * A plain bossbar with one even part.
     */
    PROGRESS
}
