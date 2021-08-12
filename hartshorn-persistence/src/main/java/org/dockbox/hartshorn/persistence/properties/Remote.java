package org.dockbox.hartshorn.persistence.properties;

import java.nio.file.Path;
import java.util.function.Function;

import lombok.Getter;

@Getter
public enum Remote {
    DERBY(Path.class,
            path -> "jdbc:derby:directory:" + path.toFile().getAbsolutePath() + "/db" + ";create=true",
            "org.apache.derby.jdbc.EmbeddedDriver"),
    ;

    private final Class<?> target;
    private final Function<?, String> urlGen;
    private final String driver;

    <T> Remote(Class<T> target, Function<T, String> urlGen, String driver) {
        this.target = target;
        this.urlGen = urlGen;
        this.driver = driver;
    }

    public PersistenceConnection connection(Object target, String user, String password) {
        return this.connection(this.url(target), user, password);
    }

    public PersistenceConnection connection(String url, String user, String password) {
        return new PersistenceConnection(url, user, password, this);
    }

    public String url(Object target) {
        if (this.target.isInstance(target)) {
            //noinspection unchecked
            return ((Function<Object, String>) this.urlGen).apply(target);
        }
        throw new IllegalArgumentException("Provided target was expected to be of type " + this.target.getSimpleName() + " but was: " + target);
    }
}
