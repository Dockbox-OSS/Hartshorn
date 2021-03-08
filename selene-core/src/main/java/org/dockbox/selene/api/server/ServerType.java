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

package org.dockbox.selene.api.server;

import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.objects.Exceptional;

/**
 * Server type definitions containing display names, minimum/preferred versions, and whether or not
 * the platform provides access to Native Minecraft Sources (NMS).
 */
public enum ServerType {
    JUNIT("JUnit Testing", true, true, "5.3.2", "5.3.2"),
    MAGMA("Magma", true, true, "Not (yet) supported", "Not (yet) supported"),
    OTHER("Other", true, false, "Not (yet) supported", "Not (yet) supported"),
    PAPER("Paper", true, false, "Not (yet) supported", "Not (yet) supported"),
    SPIGOT("Spigot", true, false, "Not (yet) supported", "Not (yet) supported"),
    SPONGE("SpongePowered", true, true, "1.12.2-2555-7.1.0-BETA-2815", "1.12.2-2838-7.2.2-RC0");

    private final String displayName;
    private final boolean hasNMSAccess;
    private final boolean isModded;
    private final String minimumVersion;
    private final String preferredVersion;

    ServerType(
            String displayName,
            boolean hasNMSAccess,
            boolean isModded,
            String minimumVersion,
            String preferredVersion) {
        this.displayName = displayName;
        this.hasNMSAccess = hasNMSAccess;
        this.isModded = isModded;
        this.minimumVersion = minimumVersion;
        this.preferredVersion = preferredVersion;
    }

    /**
     * Gets the display name of the platform in a human readable format
     *
     * @return the display name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns whether or not the platform provides access to NMS.
     *
     * @return the boolean
     */
    public boolean hasNMSAccess() {
        return this.hasNMSAccess;
    }

    /**
     * Gets minimum version.
     *
     * @return the minimum version
     */
    public String getMinimumVersion() {
        return this.minimumVersion;
    }

    /**
     * Gets preferred version.
     *
     * @return the preferred version
     */
    public String getPreferredVersion() {
        return this.preferredVersion;
    }

    /**
     * Returns whether or not the platform provides access to a mod loader. This can be especially
     * useful when using {@link FileManager#getModdedPlatformModsConfigDir()} as it may return {@link
     * Exceptional#empty()} depending on the availability mods on the platform.
     *
     * @return the boolean
     */
    public boolean isModded() {
        return this.isModded;
    }
}
