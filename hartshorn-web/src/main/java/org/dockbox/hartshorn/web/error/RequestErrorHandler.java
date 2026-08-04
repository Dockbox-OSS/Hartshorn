package org.dockbox.hartshorn.web.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface RequestErrorHandler<E extends Throwable> {

    void handle(
            E exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;
}
