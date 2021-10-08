package org.dockbox.hartshorn.web.error;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.CarrierContext;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestError extends CarrierContext {

    PrintWriter writer();
    HttpServletRequest request();
    HttpServletResponse response();

    int statusCode();
    String message();
    Exceptional<Throwable> cause();
    boolean yieldDefaults();

    RequestError message(String message);
    RequestError yieldDefaults(boolean yieldDefaults);
}
