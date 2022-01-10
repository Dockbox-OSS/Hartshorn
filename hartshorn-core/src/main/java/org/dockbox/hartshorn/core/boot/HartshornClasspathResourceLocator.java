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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.AllArgsConstructor;

/**
 * The default implementation of {@link ClasspathResourceLocator}. This implementation will copy the resource to a temporary
 * location and return the path to the temporary location.
 *
 * @author Guus Lieben
 * @since 22.1
 */
@AllArgsConstructor
public class HartshornClasspathResourceLocator implements ClasspathResourceLocator {

    private final ApplicationContext applicationContext;

    @Override
    public Exceptional<Path> resource(final String name) {
        try {
            final InputStream in = Hartshorn.class.getClassLoader().getResourceAsStream(name);
            if (in == null) {
                this.applicationContext.log().debug("Could not locate resource " + name);
                return Exceptional.empty();
            }

            final byte[] buffer = new byte[in.available()];
            final int bytes = in.read(buffer);
            if (bytes == -1) return Exceptional.of(new IOException("Requested resource contained no context"));

            final String[] parts = name.split("/");
            final String fileName = parts[parts.length - 1];
            final Path tempFile = Files.createTempFile(fileName, ".tmp");
            this.applicationContext.log().debug("Writing compressed resource " + name + " to temporary file " + tempFile.toFile().getName());
            final OutputStream outStream = new FileOutputStream(tempFile.toFile());
            outStream.write(buffer);
            outStream.flush();
            outStream.close();

            return Exceptional.of(tempFile);
        }
        catch (final NullPointerException | IOException e) {
            return Exceptional.of(e);
        }
    }
}
