package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.message.MutableHttpMessageHeaders;
import org.eclipse.jetty.http.HttpFields;

public class JettyHttpMessageHeaders implements MutableHttpMessageHeaders {

    private final HttpFields headers;

    public JettyHttpMessageHeaders(HttpFields headers) {
        this.headers = headers;
    }

    @Override
    public String get(String name) {
        return this.headers.get(name);
    }

    @Override
    public void set(String name, String value) {
        if (this.headers instanceof HttpFields.Mutable mutable) {
            mutable.put(name, value);
            return;
        }
        throw new UnsupportedOperationException("Headers are not mutable");
    }
}
