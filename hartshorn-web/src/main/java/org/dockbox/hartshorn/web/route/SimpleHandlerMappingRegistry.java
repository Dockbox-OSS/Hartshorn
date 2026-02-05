package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.util.stream.EntryStream;
import org.dockbox.hartshorn.web.HttpMethod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SimpleHandlerMappingRegistry implements HandlerMappingRegistry {

    private final Map<RouteMapping, RequestHandler> mappings = new ConcurrentHashMap<>();

    @Override
    public void add(RouteMapping mapping, RequestHandler handler) {
        this.mappings.put(mapping, handler);
    }

    @Override
    public Map<RouteMapping, RequestHandler> mappings() {
        return this.mappings;
    }

    @Override
    public Map<String, RequestHandler> mappings(HttpMethod method) {
        return EntryStream.of(this.mappings)
                .filterKeys(mapping -> mapping.method() == method)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().pathPattern(),
                        Map.Entry::getValue
                ));
    }
}
