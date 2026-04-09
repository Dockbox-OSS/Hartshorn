package org.dockbox.hartshorn.web.spec;

import java.util.Map;

public class StaticPathPartSpec implements PathPartSpec {

    private final String staticPart;

    public StaticPathPartSpec(String staticPart) {
        this.staticPart = staticPart;
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
