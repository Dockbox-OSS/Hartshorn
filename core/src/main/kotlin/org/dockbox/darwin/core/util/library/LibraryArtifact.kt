package org.dockbox.darwin.core.util.library

class LibraryArtifact(val artifactId: String, val version: String, val groupId: String) {
    var repository = ""
        private set

    constructor(groupId: String, artifactId: String, version: String, repository: String) {
        this.repository = repository
    }

}
