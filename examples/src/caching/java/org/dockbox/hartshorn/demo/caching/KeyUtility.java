package org.dockbox.hartshorn.demo.caching;

import org.dockbox.hartshorn.api.keys.Key;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.demo.caching.domain.User;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;

/**
 * A simple utility class containing a simple {@link Key} to indicate the last modification time of a
 * {@link User}. The data is stored in a local {@link Map}, however it is possible to use any data store,
 * whether it is persistent or non-persistent.
 */
public class KeyUtility {

    private static final Map<User, Long> DATA = HartshornUtils.emptyMap();

    public static final Key<User, Long> LAST_MODIFIED = Keys.builder(User.class, Long.class)
            .withGetter(DATA::get)
            .withSetter(DATA::put)
            .build();
}
