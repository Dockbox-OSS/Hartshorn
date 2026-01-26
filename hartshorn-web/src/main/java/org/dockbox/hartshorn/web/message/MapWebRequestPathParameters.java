package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

public class MapWebRequestPathParameters implements WebRequestPathParameters {

    private final Map<String, String> parameters;

    public MapWebRequestPathParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, String> asMap() {
        return Map.copyOf(this.parameters);
    }

    @Override
    public Option<String> pathParameter(String name) {
        return Option.of(this.parameters.get(name));
    }

    @Override
    public <T> Option<T> pathParameter(String name, Converter<String, T> converter) {
        return this.pathParameter(name).map(converter::convert);
    }
}
