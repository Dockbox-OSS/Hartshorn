/*
 * Copyright 2019-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.web.spec;

import java.util.Map;
import java.util.regex.Pattern;
import org.dockbox.hartshorn.util.option.Option;
import org.jspecify.annotations.Nullable;

/**
 * Specification for a path part that represents a request parameter/variable. Parameters can be
 * defined with or without a RegEx pattern constraint. For example, the path part "{id:\d+}" defines
 * a parameter named "id" that must match the RegEx pattern "\d+" (i.e. one or more digits). A part
 * without a pattern, such as "{name}", defines a parameter named "name" that can match any value.
 *
 * @param parameterName the name of the parameter defined by this path part
 * @param pattern the optional RegEx pattern constraint for this parameter, or {@code null}
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record ParameterPathPartSpec(String parameterName, Pattern pattern) implements PathPartSpec {

    public ParameterPathPartSpec(String parameterName, @Nullable Pattern pattern) {
        this.parameterName = parameterName;
        this.pattern = pattern;
    }

    /**
     * Returns the name of the parameter defined by this path part. This is the name that will be
     * used to store the value of the parameter in the path parameters map when matching a request
     * path against this specification.
     *
     * @return the name of the parameter defined by this path part
     */
    @Override
    public String parameterName() {
        return this.parameterName;
    }

    /**
     * Returns the RegEx pattern constraint for this parameter, or {@code null} if no pattern is
     * defined. If a pattern is defined, the value of this parameter must match the pattern for a
     * request path to match this specification.
     *
     * @return the RegEx pattern constraint for this parameter, or {@code null} if no pattern is
     * defined
     */
    @Override
    public Pattern pattern() {
        return this.pattern;
    }

    /**
     * Parses the given path part string into a {@link ParameterPathPartSpec} if it is in the
     * correct format (i.e. starts with <code>"{" and ends with "}"</code>). The content between the
     * braces is expected to be in the format <code>"{parameterName:pattern}"</code> or just
     * <code>"{parameterName}"</code>.
     *
     * @param part the path part string to parse
     *
     * @return an {@link Option} containing the parsed {@link ParameterPathPartSpec} if the input
     * string is in the correct format, or otherwise an empty {@link Option}
     */
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
                }
                catch (Exception e) {
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
