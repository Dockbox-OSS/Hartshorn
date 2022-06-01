package org.dockbox.hartshorn.data.annotations;

import org.dockbox.hartshorn.data.jpa.JpaRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the key of a data source for a {@link JpaRepository}. The key is used to identify the
 * data source in the {@link org.dockbox.hartshorn.data.remote.DataSourceList}, and is <b>not</b>
 * a representation of a {@link org.dockbox.hartshorn.inject.Key}.
 *
 * <p>A sample usage may look like the following snippet:
 * <pre>{@code
 * @Service
 * @DataSource("my-data-source")
 * public class EntityJpaRepository implements JpaRepository<Entity, Long> {
 * }
 * }</pre>
 *
 * @author Guus Lieben
 * @since 22.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataSource {
    String value();
}
