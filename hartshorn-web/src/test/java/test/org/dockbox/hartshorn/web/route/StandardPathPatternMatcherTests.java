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

package test.org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.route.support.PathPatternMatcher;
import org.dockbox.hartshorn.web.route.support.StandardPathPatternMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StandardPathPatternMatcherTests {

    public static Stream<Arguments> matchParameters() {
        // checkstyle:off LineLength
        return Stream.of(
                arguments("/test/path", "/test/path", true, Map.of()),
                arguments("/test/{id}/path", "/test/123/path", true, Map.of("id", "123")),
                arguments("/test/{id}/path", "/test/abc/path", true, Map.of("id", "abc")),
                arguments("/test/*/path", "/test/anyvalue/path", true, Map.of()),
                arguments("/test/{id}/{action}", "/test/123/edit", true, Map.of("id", "123", "action", "edit")),
                // No match, different static segment
                arguments("/test/{id}/path", "/test/123/other", false, Map.of()),
                // No match, extra segment
                arguments("/test/*/path", "/test/anyvalue/secondpart/path", false, Map.of()),
                // No match, missing segment
                arguments("/test/*/path", "/test/path", false, Map.of()),
                // No match, missing segment
                arguments("/test/{id}/{action}", "/test/123", false, Map.of())
        );
        // checkstyle:on LineLength
    }

    static Arguments arguments(String pattern, String route, boolean match, Map<String, String> parameters) {
        return Arguments.of(pattern, route, match, parameters);
    }

    @ParameterizedTest
    @MethodSource("matchParameters")
    void matchTests(String pattern, String route, boolean match, Map<String, String> parameters) {
        StandardPathPatternMatcher matcher = new StandardPathPatternMatcher();
        PathPatternMatcher.PatternMatch patternMatch = matcher.matches(pattern, route);
        assertThat(patternMatch.matches()).isEqualTo(match);
        assertThat(patternMatch.parameters()).isEqualTo(parameters);
    }
}
