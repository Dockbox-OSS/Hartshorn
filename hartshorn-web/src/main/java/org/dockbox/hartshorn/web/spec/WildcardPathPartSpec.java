package org.dockbox.hartshorn.web.spec;

import java.util.Map;

public class WildcardPathPartSpec implements PathPartSpec {

    private final String captureName;

    public WildcardPathPartSpec() {
        this.captureName = null;
    }

    public WildcardPathPartSpec(String captureName) {
        this.captureName = captureName;
    }

    @Override
    public String stringValue() {
        if (this.captureName != null) {
            return "{*%s}";
        }
        return "*";
    }

    @Override
    public boolean matches(String pathPart, Map<String, String> pathParameters) {
        if (this.captureName != null) {
            pathParameters.put(this.captureName, pathPart);
        }
        return true;
    }
}
