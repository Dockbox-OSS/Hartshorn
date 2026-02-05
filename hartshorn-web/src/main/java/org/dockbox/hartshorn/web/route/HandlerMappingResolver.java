package org.dockbox.hartshorn.web.route;

import jakarta.servlet.http.HttpServletRequest;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.HttpMethod;

public interface HandlerMappingResolver {

    Option<HandlerMapping> resolve(HttpMethod method, String path, HttpServletRequest request);
}
