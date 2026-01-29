package org.dockbox.hartshorn.web;

public interface HttpStatusCode {

    int code();

    HttpStatusSeries series();

    default boolean isInformational() {
        return this.series() == HttpStatusSeries.INFORMATIONAL;
    }

    default boolean isSuccessful() {
        return this.series() == HttpStatusSeries.SUCCESSFUL;
    }

    default boolean isRedirection() {
        return this.series() == HttpStatusSeries.REDIRECTION;
    }

    default boolean isClientError() {
        return this.series() == HttpStatusSeries.CLIENT_ERROR;
    }

    default boolean isServerError() {
        return this.series() == HttpStatusSeries.SERVER_ERROR;
    }

    default boolean isError() {
        return this.isClientError() || this.isServerError();
    }

    static HttpStatusCode of(int statusCode) {
        HttpStatus status = HttpStatus.of(statusCode);
        if (status != null) {
            return status;
        }
        HttpStatusSeries series = HttpStatusSeries.of(statusCode);
        return new HttpStatusCode() {
            @Override
            public int code() {
                return statusCode;
            }

            @Override
            public HttpStatusSeries series() {
                return series;
            }
        };
    }
}
