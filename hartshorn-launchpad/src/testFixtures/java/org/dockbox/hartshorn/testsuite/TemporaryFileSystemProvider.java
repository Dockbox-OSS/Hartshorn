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

package org.dockbox.hartshorn.testsuite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.dockbox.hartshorn.launchpad.environment.FileSystemProvider;

/**
 * A {@link FileSystemProvider} that uses a temporary directory for the application's files.
 */
public class TemporaryFileSystemProvider implements FileSystemProvider {

    private final Path applicationPath;

    @Override
    public Path applicationPath() {
        return this.applicationPath;
    }

    public TemporaryFileSystemProvider() {
        try {
            this.applicationPath = Files.createTempDirectory("hartshorn");
            this.applicationPath.toFile().deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create temporary directory for application files", e);
        }
    }
}
