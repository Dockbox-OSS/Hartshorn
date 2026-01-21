package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.util.option.Option;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE,
    ;

    public static Option<HttpMethod> fromString(String method) {
        for (HttpMethod httpMethod : values()) {
            if (httpMethod.name().equalsIgnoreCase(method)) {
                return Option.of(httpMethod);
            }
        }
        return Option.empty();
    }
}
