package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

public interface HttpMessageQuery {

    Option<String> get(String name);

    MultiMap<String, String> asMultiMap();

    boolean notEmpty();

    String asString();
}
