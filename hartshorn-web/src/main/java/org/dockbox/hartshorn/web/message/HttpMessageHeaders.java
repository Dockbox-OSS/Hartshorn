package org.dockbox.hartshorn.web.message;

public interface HttpMessageHeaders {

    String get(String name);

    void set(String name, String value);
}
