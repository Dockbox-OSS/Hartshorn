package org.dockbox.hartshorn.config.beta;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.ObjectMappingException;

import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

public interface SerializerFunction {

    String asString() throws ObjectMappingException;

    void to(Path path) throws ObjectMappingException;

    void to(URL url) throws ObjectMappingException;

    void to(URI uri) throws ObjectMappingException;

    void to(OutputStream stream) throws ObjectMappingException;

    FileFormat format();
}
