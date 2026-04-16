package org.dockbox.hartshorn.web.spec;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

public class WildcardPathPartSpec implements PathPartSpec {

    private final String captureName;

    public WildcardPathPartSpec() {
        this.captureName = null;
    }

    public WildcardPathPartSpec(String captureName) {
        this.captureName = captureName;
    }

    public static Option<WildcardPathPartSpec> parse(String part) {
        if (part.equals("*")) {
            return Option.of(new WildcardPathPartSpec());
        }
        if (part.startsWith("{*") && part.endsWith("}")) {
            String captureName = part.substring(2, part.length() - 1);
            if (captureName.isEmpty()) {
                return Option.empty();
            }
            return Option.of(new WildcardPathPartSpec(captureName));
        }
        return Option.empty();
    }

    @Override
    public String stringValue() {
        if (this.captureName != null) {
            return "{*%s}".formatted(this.captureName);
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
