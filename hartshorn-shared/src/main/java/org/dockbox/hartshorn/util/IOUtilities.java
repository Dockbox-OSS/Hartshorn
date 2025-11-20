/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.option.Option;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * Utility class for IO operations.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public final class IOUtilities {

    private IOUtilities() {
        // Utility class
    }

    /**
     * Checks if the provided path has a file extension.
     *
     * @param path the path to check
     *
     * @return {@code true} if the path has a file extension, {@code false} otherwise
     */
    public static boolean hasFileExtension(Path path) {
        return getFileExtension(path) != null;
    }

    /**
     * Checks if the provided URI has a file extension.
     *
     * @param uri the URI to check
     *
     * @return {@code true} if the URI has a file extension, {@code false} otherwise
     */
    public static boolean hasFileExtension(URI uri) {
        return getFileExtension(uri) != null;
    }

    /**
     * Returns the file extension of the provided path. If the path does not have a file extension,
     * {@code null} is returned.
     *
     * @param path the path to check
     *
     * @return the file extension of the provided path, or {@code null} if the path does not have a
     * file extension
     */
    public static String getFileExtension(Path path) {
        return getFileExtension(path.toUri());
    }

    /**
     * Returns the file extension of the provided URI. If the URI does not have a file extension,
     * {@code null} is returned.
     *
     * @param uri the URI to check
     *
     * @return the file extension of the provided URI, or {@code null} if the URI does not have a
     * file extension
     */
    public static String getFileExtension(URI uri) {
        String path = uri.getPath();
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == path.length() - 1) {
            return null;
        }
        return path.substring(lastDotIndex + 1);
    }

    /**
     * Checks if the provided path exists.
     *
     * @param path the path to check
     *
     * @return {@code true} if the path exists, {@code false} otherwise
     */
    public static boolean exists(Path path) {
        return path.toFile().exists();
    }

    /**
     * Checks if the provided URI exists. For URIs with the "file" scheme, this method will check if
     * the file exists. For URIs with the "jar" scheme, this method will check if the entry exists
     * in the JAR file. For other schemes, this method will always return {@code true}, as we cannot
     * determine if the resource exists.
     *
     * @param uri the URI to check
     *
     * @return {@code true} if the URI exists, {@code false} otherwise
     */
    public static boolean exists(URI uri) {
        String scheme = uri.getScheme();
        if (scheme == null || "file".equals(scheme)) {
            return exists(Path.of(uri));
        }
        if ("jar".equals(scheme)) {
            return jarEntryExists(uri);
        }
        // For other schemes, we cannot determine existence, so we assume it exists
        return true;
    }

    private static boolean jarEntryExists(URI uri) {
        String s = uri.toString();
        int separatorIndex = s.indexOf("!/");
        if (separatorIndex != -1) {
            String entryPath = s.substring(separatorIndex + 2);
            String jarPath = s.substring(4, separatorIndex);
            try (var jarFile = new JarFile(Path.of(URI.create(jarPath)).toFile())) {
                return jarFile.getJarEntry(entryPath) != null;
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Opens an {@link InputStream} for the provided URI. If the stream cannot be opened, an empty
     * {@link Option} is returned.
     *
     * @param uri the URI to open the stream for
     *
     * @return an {@link Option} containing the opened {@link InputStream}, or an empty
     * {@link Option} if the stream could not be opened
     */
    public static Option<InputStream> openStream(URI uri) {
        try {
            return Option.of(uri.toURL().openStream());
        }
        catch (Exception e) {
            return Option.empty();
        }
    }

    /**
     * Opens a {@link BufferedInputStream} for the provided URI. If the stream cannot be opened, an
     * empty {@link Option} is returned.
     *
     * @param uri the URI to open the stream for
     *
     * @return an {@link Option} containing the opened {@link BufferedInputStream}, or an empty
     * {@link Option} if the stream could not be opened
     */
    public static Option<BufferedInputStream> openBufferedStream(URI uri) {
        return openStream(uri).map(BufferedInputStream::new);
    }
}
