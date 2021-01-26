package org.dockbox.selene.core.files;

import org.dockbox.selene.core.server.properties.InjectorProperty;

public final class FileTypeProperty implements InjectorProperty<FileType> {

    public static final String KEY = "SeleneInternalFileTypeKey";
    private final FileType fileType;

    private FileTypeProperty(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public FileType getObject() {
        return this.fileType;
    }

    public static FileTypeProperty of(FileType fileType) {
        return new FileTypeProperty(fileType);
    }
}
