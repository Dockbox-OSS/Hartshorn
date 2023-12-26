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
 * A {@link FileVisitor} that walks the contents of a jar file. This delegates all file processing to a {@link
 * ClassPathScanner}, which will process the file if it is a compatible file.
 *
 * @see ClassPathScanner
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class JarFileWalker implements FileVisitor<Path> {

    private final ClassPathScanner classPathScanner;
    private final ResourceHandler handler;
    private final URLClassLoader classLoader;

    /**
     * Creates a new {@link JarFileWalker} instance. The provided {@link ClassPathScanner} will be used to process files
     * that are found in the jar file. The provided {@link ResourceHandler} and {@link URLClassLoader} will be provided
     * to the {@link ClassPathScanner} when processing files.
     *
     * @param classPathScanner The scanner to use for processing files
     * @param handler The handler that will consume the file if it is compatible
     * @param classLoader The classloader to use for loading classes from the jar file
     */
    public JarFileWalker(ClassPathScanner classPathScanner, ResourceHandler handler, URLClassLoader classLoader) {
        this.classPathScanner = classPathScanner;
        this.handler = handler;
        this.classLoader = classLoader;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        String resourceName = file.toString().substring(1);
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
