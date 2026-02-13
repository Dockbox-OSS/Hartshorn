package test.org.dockbox.hartshorn.web.util;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.web.util.RouterUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RouterUtilitiesTests {

    public static Stream<Arguments> pathCombinations() {
        List<String> apiCombinations = StringUtilities.matrix()
                .optionalSegment("/")
                .segment("api")
                .optionalSegment("/")
                .build();
        List<String> userCombinations = StringUtilities.matrix()
                .optionalSegment("/")
                .segment("user")
                .optionalSegment("/")
                .build();
        List<String> idCombinations = StringUtilities.matrix()
                .optionalSegment("/")
                .segment("{id}")
                .optionalSegment("/")
                .build();
        List<String[]> allCombinations = new ArrayList<>();
        for (String api : apiCombinations) {
            for (String user : userCombinations) {
                for (String id : idCombinations) {
                    allCombinations.add(new String[]{api, user, id});
                }
            }
        }
        return allCombinations.stream().map(parts -> {
            boolean withTrailingSlash = parts[2].endsWith("/");
            return Arguments.of(parts, "/api/user/{id}" + (withTrailingSlash ? "/" : ""));
        });
    }

    @ParameterizedTest
    @MethodSource("pathCombinations")
    void validateCombinations(String[] parts, String expected) {
        String actual = RouterUtilities.combinePaths(parts);
        assertThat(actual).isEqualTo(expected);
    }
}
