package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

public interface WebRequestPathParameters {

    Map<String, String> asMap();

    Option<String> pathParameter(String name);

    <T> Option<T> pathParameter(String name, Converter<String, T> converter);
}
