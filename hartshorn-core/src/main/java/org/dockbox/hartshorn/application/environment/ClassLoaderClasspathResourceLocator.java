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

package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.util.resources.Resources;
import org.dockbox.hartshorn.util.option.Attempt;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link ClasspathResourceLocator}. This implementation will copy the resource to a temporary
 * location and return the path to the temporary location.
 *
 * @author Guus Lieben
 * @since 0.4.9
 */
public class ClassLoaderClasspathResourceLocator implements ClasspathResourceLocator {

    private final ApplicationEnvironment environment;

    public ClassLoaderClasspathResourceLocator(ApplicationEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Attempt<Path, IOException> resource(String name) {
        return Attempt.of(() -> Resources.getResourceAsFile(name), IOException.class).map(File::toPath);
    }

    @Override
    public Set<Path> resources(String name) {
        try {
            String normalizedPath = name.replaceAll("\\\\", "/");
            int lastIndex = normalizedPath.lastIndexOf("/");
            String path = lastIndex == -1 ? "" : normalizedPath.substring(0, lastIndex + 1);
            String fileName = lastIndex == -1 ? normalizedPath : normalizedPath.substring(lastIndex + 1);

            Set<File> files = new HashSet<>();
            Set<File> resources = Resources.getResourcesAsFiles(path);
            for (File parent : resources) {
                File[] filteredResources = parent.listFiles((dir, file) -> file.startsWith(fileName));
                if (filteredResources != null) {
                    files.addAll(Arrays.asList(filteredResources));
                }
            }
            return files.stream().map(File::toPath).collect(Collectors.toUnmodifiableSet());
        }
        catch (NullPointerException | IOException e) {
            return new HashSet<>();
        }
    }

    @Override
    public URI classpathUri() {
        try {
            URL resource = Hartshorn.class.getClassLoader().getResource("");
            if (resource == null) {
                return null;
            }
            return resource.toURI();
        }
        catch (URISyntaxException e) {
            this.environment.handle("Could not look up classpath base", e);
            return null;
        }
    }
}
