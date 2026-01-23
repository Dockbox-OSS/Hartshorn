package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.message.MutableHttpMessageHeaders;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;

import java.util.Map;

public class JettyHttpMessageHeaders implements MutableHttpMessageHeaders {

    private final HttpFields headers;

    public JettyHttpMessageHeaders(HttpFields headers) {
        this.headers = headers;
    }

    @Override
    public Option<String> get(String name) {
        return Option.of(this.headers.get(name));
    }

    @Override
    public Map<String, String> asMap() {
        return this.headers.stream()
                .collect(java.util.stream.Collectors.toMap(
                        HttpField::getName,
                        HttpField::getValue
                ));
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
