package org.dockbox.hartshorn.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH,
    ;

    public HttpMethod of(HttpServletRequest request) {
        return HttpMethod.valueOf(request.getMethod().toUpperCase(Locale.ROOT));
    }
}
