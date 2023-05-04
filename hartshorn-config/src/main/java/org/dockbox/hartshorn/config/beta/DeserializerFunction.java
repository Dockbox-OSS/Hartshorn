package org.dockbox.hartshorn.config.beta;

import org.dockbox.hartshorn.util.option.Option;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface DeserializerFunction<T> {
    Option<T> from(String content);

    Option<T> from(Path path);

    Option<T> from(URL url);

    Option<T> from(InputStream stream);
}
