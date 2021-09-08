package org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.persistence.DefaultAbstractFileManager;
import org.dockbox.hartshorn.persistence.FileManager;

import java.nio.file.Path;
import java.nio.file.Paths;

@Binds(FileManager.class)
public class LocalFileManager extends DefaultAbstractFileManager {

    @Override
    public Path data() {
        return this.root().resolve("data");
    }

    @Override
    public Path logs() {
        return this.root().resolve("logs");
    }

    @Override
    public Path root() {
        return Paths.get("");
    }

    @Override
    public Path configs() {
        return this.root().resolve("config");
    }
}
