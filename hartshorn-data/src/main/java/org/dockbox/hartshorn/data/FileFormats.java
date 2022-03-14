/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.data;

import java.nio.file.Path;

/** Enumerated values containing the file extensions for several commonly used file types. */
public enum FileFormats implements FileFormat {
    // Raw/text types
    YAML(DataStorageType.RAW, "yml", "yaml"),
    JSON(DataStorageType.RAW, "json"),
    XML(DataStorageType.RAW, "xml"),
    TOML(DataStorageType.RAW, "toml"),
    PROPERTIES(DataStorageType.RAW, "properties");

    private final String extension;
    private final DataStorageType type;
    private final String[] aliases;

    FileFormats(final DataStorageType type, final String extension, final String... aliases) {
        this.extension = extension;
        this.type = type;
        this.aliases = aliases;
    }

    public String extension() {
        return this.extension;
    }

    public DataStorageType type() {
        return this.type;
    }

    public static FileFormats lookup(final String source) {
        final int i = source.lastIndexOf('.');
        if (i == -1) {
            return null;
        }
        final String extension = source.substring(i + 1);
        for (final FileFormats format : FileFormats.values()) {
            if (format.extension.equalsIgnoreCase(extension)) {
                return format;
            } else {
                for (final String alias : format.aliases) {
                    if (alias.equalsIgnoreCase(extension)) {
                        return format;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Converts a given filename (without the file extension present), combined with a {@link Path}
     * reference to a directory, to a new {@link Path} reference to a file. If the file did not yet
     * exist, it is created.
     *
     * <p>Assumes the parent {@link Path} already exists.
     *
     * @param parent The parent directory
     * @param file The filename without a file extension present
     *
     * @return The {@link Path} reference to a file
     */
    public Path asPath(final Path parent, final String file) {
        return parent.resolve(this.asFileName(file));
    }

    /**
     * Converts a given filename (without the file extension present) to a {@link String} value
     * holding the correct format.
     *
     * @param file The filename without a file extension present
     *
     * @return The generated filename with extension
     */
    @Override
    public String asFileName(final String file) {
        return file + '.' + this.extension;
    }
}
