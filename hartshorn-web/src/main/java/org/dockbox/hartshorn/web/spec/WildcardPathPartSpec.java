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

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

/**
 * Specification for a path part that represents a wildcard match. A wildcard part matches any value
 * for the corresponding part of a request path, and can optionally capture the matched value as a
 * path parameter. For example, the path part "{*path}" defines a wildcard that captures the matched
 * value as a path parameter named {@code path}. A part defined as "*" matches any value but does
 * not capture it.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class WildcardPathPartSpec implements PathPartSpec {

    private final String captureName;

    public WildcardPathPartSpec() {
        this.captureName = null;
    }

    public WildcardPathPartSpec(String captureName) {
        this.captureName = captureName;
    }

    /**
     * Returns the name of the path parameter that this wildcard part captures, or {@code null} if
     * this wildcard part does not capture a parameter.
     *
     * @return the name of the path parameter that this wildcard part captures, or {@code null}
     */
    public String captureName() {
        return captureName;
    }

    /**
     * Parses the given path part as a wildcard path part specification. A valid wildcard path part
     * is either a single asterisk ("*"), which matches any value but does not capture it, or a part
     * that starts with "{*", ends with "}", and contains a non-empty capture name in between, which
     * matches any value and captures it as a path parameter with the specified name.
     *
     * @param part the path part to parse as a wildcard path part specification
     *
     * @return an {@link Option} containing a new {@link WildcardPathPartSpec} instance if the given
     * part is valid, or otherwise an empty {@link Option}
     */
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
