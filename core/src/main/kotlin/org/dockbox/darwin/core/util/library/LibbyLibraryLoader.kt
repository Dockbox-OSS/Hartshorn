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
