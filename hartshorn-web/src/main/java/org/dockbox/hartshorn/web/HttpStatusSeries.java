package org.dockbox.hartshorn.web;

public enum HttpStatusSeries {
    INFORMATIONAL,
    SUCCESSFUL,
    REDIRECTION,
    CLIENT_ERROR,
    SERVER_ERROR,
    UNKNOWN,
    ;

    public static HttpStatusSeries of(int code) {
        int seriesCode = code / 100;
        return switch (seriesCode) {
            case 1 -> INFORMATIONAL;
            case 2 -> SUCCESSFUL;
            case 3 -> REDIRECTION;
            case 4 -> CLIENT_ERROR;
            case 5 -> SERVER_ERROR;
            default -> UNKNOWN;
        };
    }
}
