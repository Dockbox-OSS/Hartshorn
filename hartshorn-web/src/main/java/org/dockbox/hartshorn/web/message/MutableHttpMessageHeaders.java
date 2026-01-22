package org.dockbox.hartshorn.web.message;

public interface MutableHttpMessageHeaders extends HttpMessageHeaders {

    void set(String name, String value);
}
