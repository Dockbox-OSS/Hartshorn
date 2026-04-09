package org.dockbox.hartshorn.web.spec;

import java.util.Map;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ParameterPathPartSpec implements PathPartSpec {

    private final String parameterName;
    private final Pattern pattern;

    public ParameterPathPartSpec(String parameterName, @Nullable Pattern pattern) {
        this.parameterName = parameterName;
        this.pattern = pattern;
    }

    @Override
    public String stringValue() {
        String patternString = this.pattern != null ? ":" + this.pattern.pattern() : "";
        return "{%s%s}".formatted(this.parameterName, patternString);
    }

    @Override
    public boolean matches(String pathPart, Map<String, String> pathParameters) {
        if (this.pattern != null && !this.pattern.matcher(pathPart).matches()) {
            return false;
        }
        pathParameters.put(this.parameterName, pathPart);
        return true;
    }
}
