package org.dockbox.darwin.core.util.library;

import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;

import org.jetbrains.annotations.NotNull;

public class LibbyLibraryLoader implements LibraryLoader<LibraryManager> {

    @Override
    public void configure(LibraryManager loader, @NotNull LibraryArtifact @NotNull [] artifacts) {
        loader.addMavenCentral();
        loader.addJCenter();
        loader.addJitPack();
        loader.addMavenLocal();
        loader.addSonatype();
        for (LibraryArtifact artifact : artifacts) {
            if (!"".equals(artifact.getRepository())) loader.addRepository(artifact.getRepository());
            loader.loadLibrary(Library.builder()
                    .groupId(artifact.getGroupId())
                    .artifactId(artifact.getArtifactId())
                    .version(artifact.getVersion())
                    .build());
        }
    }
}
