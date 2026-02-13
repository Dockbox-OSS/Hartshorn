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
