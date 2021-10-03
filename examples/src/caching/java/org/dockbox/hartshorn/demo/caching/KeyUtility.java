package org.dockbox.hartshorn.demo.caching;

import org.dockbox.hartshorn.api.keys.Key;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.demo.caching.domain.User;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;

public class KeyUtility {

    private static final Map<User, Long> DATA = HartshornUtils.emptyMap();

    public static final Key<User, Long> LAST_MODIFIED = Keys.builder(User.class, Long.class)
            .withGetter(DATA::get)
            .withSetter(DATA::put)
            .build();
}
