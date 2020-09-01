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

package org.dockbox.darwin.core.util.library

import net.byteflux.libby.Library
import net.byteflux.libby.LibraryManager

class LibbyLibraryLoader : LibraryLoader<LibraryManager?> {
    override fun configure(loader: LibraryManager?, artifacts: Array<LibraryArtifact>) {
        assert(loader != null) { "LibraryManager was absent" }
        loader!!
        with(loader) {
            addMavenCentral()
            addJCenter()
            addJitPack()
            addMavenLocal()
            addSonatype()
        }
        artifacts.forEach { artifact ->
            if ("" != artifact.repository) loader.addRepository(artifact.repository)
            loader.loadLibrary(Library.builder()
                    .groupId(artifact.groupId)
                    .artifactId(artifact.artifactId)
                    .version(artifact.version)
                    .build())
        }
    }
}
