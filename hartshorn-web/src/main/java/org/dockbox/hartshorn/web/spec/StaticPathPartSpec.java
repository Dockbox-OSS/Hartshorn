package org.dockbox.hartshorn.web.spec;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

public class StaticPathPartSpec implements PathPartSpec {

    private final String staticPart;

    public StaticPathPartSpec(String staticPart) {
        this.staticPart = staticPart;
    }

    public static Option<StaticPathPartSpec> parse(String part) {
        if (part.isEmpty() || !part.matches("[\\w-_]+")) {
            return Option.empty();
        }
        return Option.of(new StaticPathPartSpec(part));
    }

    @Override
    public String stringValue() {
        return this.staticPart;
    }

    @Override
    public boolean matches(String pathPart, Map<String, String> pathParameters) {
        return this.staticPart.equals(pathPart);
    }
}
