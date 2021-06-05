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

package org.dockbox.hartshorn.api;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** The global {@link Hartshorn} instance used to grant access to various components. */
public final class Hartshorn {

    private static final Map<String, Logger> LOGGERS = HartshornUtils.emptyConcurrentMap();

    private Hartshorn() {}

    /**
     * Gets the instance of {@link Hartshorn}.
     *
     * @return The {@link Hartshorn} instance
     */
    public static HartshornBootstrap server() {
        return HartshornBootstrap.instance();
    }

    public static ApplicationContext context() {
        return server().getContext();
    }

    /**
     * Gets a log instance representing the calling type.
     *
     * @return The {@link Logger}
     */
    public static Logger log() {
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        String className = element.getClassName();
        if (LOGGERS.containsKey(className)) return LOGGERS.get(className);

        String[] qualifiedClassName = className.split("\\.");
        StringBuilder fullName = new StringBuilder();
        for (int i = 0; i < qualifiedClassName.length; i++) {
            String part = qualifiedClassName[i];
            if (i > 0) fullName.append('.');
            if (i == qualifiedClassName.length-1) fullName.append(part);
            else fullName.append(part.charAt(0));
        }
        String name = HartshornUtils.wrap(fullName.toString(), 35);
        Logger logger = LoggerFactory.getLogger(name);
        LOGGERS.put(className, logger);
        return logger;
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
            InputStream in = Hartshorn.class.getClassLoader().getResourceAsStream(name);
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
