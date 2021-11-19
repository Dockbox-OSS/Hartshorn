package org.dockbox.hartshorn.web.mvc;

import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileViewTemplate implements ViewTemplate {
    private final Path path;
}
