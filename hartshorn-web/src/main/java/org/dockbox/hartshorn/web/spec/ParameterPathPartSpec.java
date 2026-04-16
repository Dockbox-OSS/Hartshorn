package org.dockbox.hartshorn.web.spec;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.regex.Pattern;

public class ParameterPathPartSpec implements PathPartSpec {

    private final String parameterName;
    private final Pattern pattern;

    public ParameterPathPartSpec(String parameterName, @Nullable Pattern pattern) {
        this.parameterName = parameterName;
        this.pattern = pattern;
    }

    public String parameterName() {
        return parameterName;
    }

    public Pattern pattern() {
        return pattern;
    }

    public static Option<ParameterPathPartSpec> parse(String part) {
        if (part.startsWith("{") && part.endsWith("}")) {
            if (part.contains("*")) {
                // Wildcard part
                return Option.empty();
            }
            String content = part.substring(1, part.length() - 1);
            String[] parts = content.split(":", 2);
            String parameterName = parts[0];
            if (parameterName.isEmpty()) {
                return Option.empty();
            }
            Pattern pattern = null;
            if (parts.length > 1) {
                try {
                    pattern = Pattern.compile(parts[1]);
                } catch (Exception e) {
                    return Option.empty();
                }
            }
            return Option.of(new ParameterPathPartSpec(parameterName, pattern));
        }
        return Option.empty();
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
