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

package org.dockbox.hartshorn.application.environment;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A {@link PathFileSystemProvider} that uses the current working directory as the root.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public class PathFileSystemProvider implements FileSystemProvider {

    @Override
    public Path applicationPath() {
        // To absolute path, to make sure we don't get a relative path which may cause
        // issues with looking up parent directories.
        return Paths.get("").toAbsolutePath();
    }
}
