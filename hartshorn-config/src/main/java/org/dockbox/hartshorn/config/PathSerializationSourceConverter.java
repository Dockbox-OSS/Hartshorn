/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.application.environment.FileSystemProvider;
import org.dockbox.hartshorn.config.annotations.FileSource;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.option.Option;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TODO: #1062 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class PathSerializationSourceConverter implements SerializationSourceConverter {

    private final FileSystemProvider fileSystem;

    public PathSerializationSourceConverter(FileSystemProvider fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public InputStream inputStream(AnnotatedElementView context, Object... args) {
        return this.resolvePath(context)
                .map(path -> {
                    try {
                        return Files.newInputStream(path);
                    }
                    catch (IOException e) {
                        return null;
                    }
                })
                .map(BufferedInputStream::new)
                .orNull();
    }

    @Override
    public OutputStream outputStream(AnnotatedElementView context, Object... args) {
        return this.resolvePath(context).map(path -> {
                    try {
                        return Files.newOutputStream(path);
                    }
                    catch (IOException e) {
                        return null;
                    }
                }).map(BufferedOutputStream::new)
                .orNull();
    }

    private Option<Path> resolvePath(AnnotatedElementView context) {
        return context.annotations().get(FileSource.class)
                .map(fileSource -> {
                    if (fileSource.relativeToApplicationPath()) {
                        return this.fileSystem.applicationPath().resolve(fileSource.value());
                    }
                    return Paths.get(fileSource.value());
                });
    }
}
