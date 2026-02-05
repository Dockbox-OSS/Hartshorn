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

package org.dockbox.hartshorn.web.route.support;

import java.util.Map;

/**
 * A utility class for matching URL path patterns against incoming web requests.
 * This class supports various pattern formats, including static paths, wildcard
 * segments, and parameterized segments.
 *
 * <p>For example:
 * - Static path: /users/profile
 * - Wildcard path: /users/*
 * - Parameterized path: /users/{id}
 *
 * <p>This class provides methods to determine if a given request path matches
 * a specified pattern and to extract any parameters from the path.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StandardPathPatternMatcher implements PathPatternMatcher {

    @Override
    public PatternMatch matches(String pattern, String path) {
        // Filter out leading and trailing slashes for consistency
        pattern = filterPattern(pattern);
        path = filterPattern(path);
        if (pattern.equals(path)) {
            return new PatternMatch(true, Map.of());
        }

        String[] patternSegments = pattern.split("/");
        String[] pathSegments = path.split("/");

        if (patternSegments.length != pathSegments.length) {
            return new PatternMatch(false, Map.of());
        }

        Map<String, String> parameters = new java.util.HashMap<>();

        for (int i = 0; i < patternSegments.length; i++) {
            String patternSegment = patternSegments[i];
            String pathSegment = pathSegments[i];

            if (patternSegment.equals("*")) {
                continue; // Wildcard matches any segment
            } else if (patternSegment.startsWith("{") && patternSegment.endsWith("}")) {
                String paramName = patternSegment.substring(1, patternSegment.length() - 1);
                parameters.put(paramName, pathSegment);
            } else if (!patternSegment.equals(pathSegment)) {
                return new PatternMatch(false, Map.of());
            }
        }
        return new PatternMatch(true, parameters);
    }

    private String filterPattern(String pattern) {
        if (pattern.startsWith("/")) {
            pattern = pattern.substring(1);
        }
        if (pattern.endsWith("/")) {
            pattern = pattern.substring(0, pattern.length() - 1);
        }
        return pattern;
    }

    /**
     * Represents the result of a path pattern match operation.
     *
     * @param matches Indicates whether the path matches the pattern.
     * @param parameters A map of extracted parameters if the path matches the pattern.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public record MatchResult(boolean matches, Map<String, String> parameters) { }
}
