/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.introspect.scan.classpath;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A {@link FileVisitor} that walks the contents of a directory. This delegates all file processing to a {@link
 * ClassPathScanner}, which will process the file if it is a compatible file.
 *
 * @see ClassPathScanner
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class DirectoryFileTreeWalker implements FileVisitor<Path> {

    private final ClassPathScanner classPathScanner;
    private final int rootDirNameLength;
    private final ResourceHandler handler;
    private final URLClassLoader classLoader;

    public DirectoryFileTreeWalker(ClassPathScanner classPathScanner, int rootDirNameLength,
                                   ResourceHandler handler, URLClassLoader classLoader) {
        this.classPathScanner = classPathScanner;
        this.rootDirNameLength = rootDirNameLength;
        this.handler = handler;
        this.classLoader = classLoader;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String resourceName = file.toFile().getCanonicalPath().substring(this.rootDirNameLength + 1);
        this.classPathScanner.processPathResource(this.handler, this.classLoader, resourceName, file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
}
