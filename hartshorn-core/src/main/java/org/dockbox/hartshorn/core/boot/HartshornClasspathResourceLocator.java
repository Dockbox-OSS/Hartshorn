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
