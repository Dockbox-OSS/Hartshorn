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
