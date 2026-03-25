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

package test.org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.web.HttpStatusSeries;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class HttpStatusSeriesTests {

    public static Stream<Arguments> statusCodesAndSeries() {
        return Stream.of(
                Arguments.of(100, HttpStatusSeries.INFORMATIONAL),
                Arguments.of(150, HttpStatusSeries.INFORMATIONAL),
                Arguments.of(199, HttpStatusSeries.INFORMATIONAL),
                Arguments.of(200, HttpStatusSeries.SUCCESSFUL),
                Arguments.of(250, HttpStatusSeries.SUCCESSFUL),
                Arguments.of(299, HttpStatusSeries.SUCCESSFUL),
                Arguments.of(300, HttpStatusSeries.REDIRECTION),
                Arguments.of(350, HttpStatusSeries.REDIRECTION),
                Arguments.of(399, HttpStatusSeries.REDIRECTION),
                Arguments.of(400, HttpStatusSeries.CLIENT_ERROR),
                Arguments.of(450, HttpStatusSeries.CLIENT_ERROR),
                Arguments.of(499, HttpStatusSeries.CLIENT_ERROR),
                Arguments.of(500, HttpStatusSeries.SERVER_ERROR),
                Arguments.of(550, HttpStatusSeries.SERVER_ERROR),
                Arguments.of(599, HttpStatusSeries.SERVER_ERROR),
                Arguments.of(99, HttpStatusSeries.UNKNOWN),
                Arguments.of(600, HttpStatusSeries.UNKNOWN)
        );
    }

    @ParameterizedTest
    @MethodSource("statusCodesAndSeries")
    void seriesCanBeResolvedFromStatusCode(int statusCode, HttpStatusSeries series) {
        HttpStatusSeries resolved = HttpStatusSeries.of(statusCode);
        assert resolved == series;
    }
}
