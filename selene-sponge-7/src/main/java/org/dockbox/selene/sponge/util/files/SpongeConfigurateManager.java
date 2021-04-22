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

package org.dockbox.selene.sponge.util.files;

import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.files.FileType;
import org.dockbox.selene.common.files.DefaultConfigurateManager;

/**
 * Uses SpongeDefaultFileManager to determine the directory paths. This way all directory paths can
 * be reused by other FileManager implementations without the need of redefining these when changes
 * are made. Due to multiple inheritance not being possible in Java, a interface is used for this
 * purpose, requiring us to directly target the interface super.
 */
public class SpongeConfigurateManager extends DefaultConfigurateManager implements SpongeDefaultFileManager {

    /** Provides the given {@link FileType} to the super type {@link FileManager}. */
    protected SpongeConfigurateManager() {
        super(FileType.YAML);
    }
}
