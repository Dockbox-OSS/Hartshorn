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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ClassPathScanner {

    private final Set<String> classNames = new HashSet<>();
    private final Set<ClassLoader> classLoaders = new HashSet<>();
    private final Set<String> prefixFilters = new HashSet<>();

    private boolean resourcesOnly = false;
    private boolean classesOnly = true;
    private boolean excludeInnerClasses = false;
    private long scanTime = -1;

    private ClassPathScanner() {
        // Private constructor to prevent instantiation outside of #create()
    }

    public static ClassPathScanner create() {
        return new ClassPathScanner();
    }

    public ClassPathScanner addSystemPropertyPaths(final String key) {
        final String value = System.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return this;
        }

        for (final String path : value.split(String.valueOf(File.pathSeparatorChar), -1)) {
            if (path == null || path.trim().isEmpty()) continue;
            final File file = new File(path);
            if (!file.exists()) continue;

            try {
                final URL url = file.toURI().toURL();
                this.classLoaders.add(new URLClassLoader(new URL[] { url }) {
                    public String toString() {
                        return super.toString() + " [url=" + url.toExternalForm() + "]";
                    }
                });
            }
            catch (final MalformedURLException e) {
                // Ignore
            }
        }
        return this;
    }

    public ClassPathScanner scan(final ResourceHandler handler) throws ClassPathWalkingException {
        this.reset();

        final long start = System.currentTimeMillis();
        for (final ClassLoader classLoader : this.classLoaders) {
            if (classLoader instanceof URLClassLoader) {
                this.scanClassLoaderResources(handler, (URLClassLoader) classLoader);
            }
            else {
                throw new ClassPathWalkingException("Classloader " + classLoader + " is not supported");
            }
        }

        this.scanTime = System.currentTimeMillis() - start;
        return this;
    }

    private void reset() {
        this.classNames.clear();
    }

    private void scanClassLoaderResources(final ResourceHandler handler, final URLClassLoader classLoader) throws ClassPathWalkingException {
        for (final URL url : classLoader.getURLs()) {
            if (url.getFile() != null && !url.getFile().isEmpty()) {

                final File file = new File(url.getFile());
                if (file.exists()) {
                    if (file.isDirectory()) {
                        this.processDirectoryResource(handler, classLoader, file);
                    }
                    else if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                        this.processJarFileResource(handler, classLoader, url, file);
                    }
                }
                else {
                    throw new ClassPathWalkingException("Unsupported classpath resource: " + url);
                }
            }
        }
    }

    private void processJarFileResource(final ResourceHandler handler, final URLClassLoader classLoader, final URL url, final File jarFile)
            throws ClassPathWalkingException {
        try {
            final FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(url.toURI()), (ClassLoader) null);
            Files.walkFileTree(fileSystem.getRootDirectories().iterator().next(), new JarFileWalker(this, handler, classLoader));
        }
        catch (final IOException | URISyntaxException e) {
            throw new ClassPathWalkingException("Error while scanning jar file " + jarFile, e);
        }
    }

    private void processDirectoryResource(final ResourceHandler handler, final URLClassLoader classLoader, final File directory) throws ClassPathWalkingException {
        try {
            final File rootDir = directory.getCanonicalFile();
            final int rootDirNameLen = rootDir.getCanonicalPath().length();
            Files.walkFileTree(rootDir.toPath(), new DirectoryFileTreeWalker(this, rootDirNameLen, handler, classLoader));
        }
        catch (final IOException e) {
            throw new ClassPathWalkingException("Could not process directory resource " + directory.getPath(), e);
        }
    }

    public void processPathResource(final ResourceHandler handler, final URLClassLoader classLoader, final String resourceName, final Path path) {
        if (handler == null) return;

        final boolean isClassResource = resourceName.toLowerCase().endsWith(".class");
        final String checkedResourceName = !isClassResource
                ? resourceName
                : this.resourceToCanonicalName(resourceName);

        if (isClassResource && this.classesOnly && this.classNames.contains(checkedResourceName)) return;

        if (this.classesOnly && !isClassResource) return;
        if (this.resourcesOnly && isClassResource) return;
        if (this.excludeInnerClasses && isClassResource && checkedResourceName.indexOf('$') > -1) return;

        for (final String beginFilterName : this.prefixFilters) {
            if (!checkedResourceName.startsWith(beginFilterName)) return;
        }

        final ClassPathResource resource = new ClassCandidateResource(classLoader, path, checkedResourceName, isClassResource);
        handler.handle(resource);
    }

    @NonNull
    private String resourceToCanonicalName(final String resourceName) {
        return resourceName.substring(0, resourceName.length() - 6)
                .replace('/', '.')
                .replace('\\', '.');
    }

    public ClassPathScanner filterPrefix(final String prefix) {
        if (prefix != null) {
            this.prefixFilters.add(prefix);
        }
        return this;
    }

    public ClassPathScanner includeDefaultClassPath() {
        this.addSystemPropertyPaths("java.class.path");
        return this;
    }

    public ClassPathScanner classesOnly() {
        this.classesOnly = true;
        this.resourcesOnly = false;
        return this;
    }

    public ClassPathScanner resourcesOnly() {
        this.classesOnly = false;
        this.resourcesOnly = true;
        return this;
    }

    public ClassPathScanner excludeInnerClasses() {
        this.excludeInnerClasses = true;
        return this;
    }

    public Set<ClassLoader> classLoaders() {
        return Collections.unmodifiableSet(this.classLoaders);
    }

    public long scanTime() {
        return this.scanTime;
    }
}
