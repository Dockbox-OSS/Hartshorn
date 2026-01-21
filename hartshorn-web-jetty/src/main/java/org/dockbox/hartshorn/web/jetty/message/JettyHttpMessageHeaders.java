package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.message.HttpMessageHeaders;
import org.eclipse.jetty.http.HttpFields;

public class JettyHttpMessageHeaders implements HttpMessageHeaders {

    private final HttpFields.Mutable headers;

    public JettyHttpMessageHeaders(HttpFields.Mutable headers) {
        this.headers = headers;
    }

    @Override
    public String get(String name) {
        return this.headers.get(name);
    }

    @Override
    public void set(String name, String value) {
        this.headers.put(name, value);
    }
}
