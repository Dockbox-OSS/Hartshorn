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
