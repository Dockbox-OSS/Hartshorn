package org.dockbox.hartshorn.web.route;

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
 * @see PathRouteRegistry
 * @see PathMatchingRouterPathConfigurer
 *
 * @author Guus Lieben
 *
 * @since 0.7.0
 */
public class PathPatternMatcher {

    public static MatchResult match(String pattern, String path) {
        // Filter out leading and trailing slashes for consistency
        pattern = filterPattern(pattern);
        path = filterPattern(path);
        if (pattern.equals(path)) {
            return new MatchResult(true, Map.of());
        }

        String[] patternSegments = pattern.split("/");
        String[] pathSegments = path.split("/");

        if (patternSegments.length != pathSegments.length) {
            return new MatchResult(false, Map.of());
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
                return new MatchResult(false, Map.of());
            }
        }

        return new MatchResult(true, parameters);
    }

    private static String filterPattern(String pattern) {
        if (pattern.startsWith("/")) {
            pattern = pattern.substring(1);
        }
        if (pattern.endsWith("/")) {
            pattern = pattern.substring(0, pattern.length() - 1);
        }
        return pattern;
    }

    public record MatchResult(boolean matches, Map<String, String> parameters) { }
}
