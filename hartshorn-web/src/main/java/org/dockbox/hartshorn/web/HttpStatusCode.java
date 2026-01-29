package org.dockbox.hartshorn.web;

public interface HttpStatusCode {

    int code();

    HttpStatusSeries series();

    boolean isInformational();

    boolean isSuccessful();

    boolean isRedirection();

    boolean isClientError();

    boolean isServerError();

    boolean isError();
}
