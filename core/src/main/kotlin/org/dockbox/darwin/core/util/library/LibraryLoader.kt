package org.dockbox.darwin.core.util.library

interface LibraryLoader<L> {

    fun configure(loader: L, artifacts: Array<LibraryArtifact>)

}
