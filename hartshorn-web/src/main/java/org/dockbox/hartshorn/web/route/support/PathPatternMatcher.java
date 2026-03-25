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
 * Interface for matching request paths against path patterns, typically used in routing to
 * determine which handler should process a given request based on its path. Implementations of this
 * interface should be able to match patterns that include path variables (e.g., /users/{id}) and
 * extract those variables into a map of parameters.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface PathPatternMatcher {

    /**
     * Matches the given pattern against the specified path.
     *
     * @param pattern The path pattern to match against (e.g., /users/{id}).
     * @param path The actual request path (e.g., /users/123).
     *
     * @return A {@link StandardPathPatternMatcher.MatchResult} indicating whether the path matches
     * the pattern and any extracted parameters.
     */
    PatternMatch matches(String pattern, String path);

    /**
     * Result of a path pattern match, containing whether the pattern matches the path and any
     * extracted parameters from the path.
     *
     * @param matches Whether the pattern matches the path
     * @param parameters A map of parameter names to their corresponding values extracted from the
     * path, if the pattern matches. If the pattern does not match, this map should be empty.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    record PatternMatch(boolean matches, Map<String, String> parameters) { }
}
