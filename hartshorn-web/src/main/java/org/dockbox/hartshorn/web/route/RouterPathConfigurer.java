package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

public interface RouterPathConfigurer {

    RouterPathConfigurer request(HttpMethod method, String path, RequestHandler handler);

    default RouterPathConfigurer get(String path, RequestHandler handler) {
        return this.request(HttpMethod.GET, path, handler);
    }

    default RouterPathConfigurer post(String path, RequestHandler handler) {
        return this.request(HttpMethod.POST, path, handler);
    }

    default RouterPathConfigurer put(String path, RequestHandler handler) {
        return this.request(HttpMethod.PUT, path, handler);
    }

    default RouterPathConfigurer delete(String path, RequestHandler handler) {
        return this.request(HttpMethod.DELETE, path, handler);
    }
}
