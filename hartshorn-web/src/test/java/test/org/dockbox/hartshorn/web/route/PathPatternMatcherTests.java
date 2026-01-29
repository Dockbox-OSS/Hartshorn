package test.org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.route.PathPatternMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PathPatternMatcherTests {

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
        PathPatternMatcher.MatchResult result = PathPatternMatcher.match(
                pattern,
                route
        );
        assertThat(result.matches()).isEqualTo(match);
        assertThat(result.parameters()).isEqualTo(parameters);
    }
}
