/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.api;

import org.dockbox.selene.api.domain.Exceptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** The global {@link Selene} instance used to grant access to various components. */
public final class Selene {

    private Selene() {}

    /**
     * Gets the instance of {@link Selene}.
     *
     * @return The {@link Selene} instance
     */
    public static SeleneBootstrap getServer() {
        return SeleneBootstrap.getInstance();
    }

    /**
     * Gets a log instance representing the calling type.
     *
     * @return The {@link Logger}
     */
    public static Logger log() {
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        String[] qualifiedClassName = element.getClassName().split("\\.");
        return LoggerFactory.getLogger(SeleneInformation.PROJECT_NAME + '/' + qualifiedClassName[qualifiedClassName.length - 1]);
    }

    /**
     * Attempts to look up a resource file. If the file exists it is wrapped in a {@link Exceptional}
     * and returned. If the file does not exist or is a directory, {@link Exceptional#none()} is
     * returned. If the requested file name is invalid, or {@code null}, a {@link Exceptional}
     * containing the appropriate exception is returned.
     *
     * @param name
     *         The name of the file to look up
     *
     * @return The resource file wrapped in a {@link Exceptional}, or a appropriate {@link
     *         Exceptional} (either none or providing the appropriate exception).
     */
    public static Exceptional<Path> getResourceFile(String name) {
        try {
            InputStream in = Selene.class.getClassLoader().getResourceAsStream(name);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);

            Path tempFile = Files.createTempFile(name, ".tmp");
            OutputStream outStream = new FileOutputStream(tempFile.toFile());
            outStream.write(buffer);

            return Exceptional.of(tempFile);
        }
        catch (NullPointerException | IOException e) {
            return Exceptional.of(e);
        }
    }
}
