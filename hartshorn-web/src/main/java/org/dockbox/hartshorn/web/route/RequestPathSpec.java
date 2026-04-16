package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.spec.PathSpec;

public record RequestPathSpec(
        PathSpec pathSpec,
        HttpMethod method
) {
}
