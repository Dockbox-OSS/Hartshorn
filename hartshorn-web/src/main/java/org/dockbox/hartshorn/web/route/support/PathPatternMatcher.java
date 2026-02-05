package org.dockbox.hartshorn.web.route.support;

import java.util.Map;

public interface PathPatternMatcher {

    /**
     * Matches the given pattern against the specified path.
     *
     * @param pattern The path pattern to match against (e.g., /users/{id}).
     * @param path The actual request path (e.g., /users/123).
     *
     * @return A {@link StandardPathPatternMatcher.MatchResult} indicating whether the path matches the pattern and any
     * extracted parameters.
     */
    PatternMatch matches(String pattern, String path);

    record PatternMatch(boolean matches, Map<String, String> parameters) { }
}
