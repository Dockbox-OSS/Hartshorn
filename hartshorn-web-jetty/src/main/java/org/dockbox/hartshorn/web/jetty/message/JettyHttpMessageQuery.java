package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.message.HttpMessageQuery;
import org.eclipse.jetty.util.Fields;

public class JettyHttpMessageQuery implements HttpMessageQuery {
    private final Fields queryParameters;

    public JettyHttpMessageQuery(Fields queryParameters) {
        this.queryParameters = queryParameters;
    }

    @Override
    public Option<String> get(String name) {
        return Option.of(this.queryParameters.getValue(name));
    }

    @Override
    public MultiMap<String, String> asMultiMap() {
        MultiMap<String, String> entries = new ArrayListMultiMap<>();
        this.queryParameters.toMultiMap().forEach(entries::putAll);
        return entries;
    }
}
