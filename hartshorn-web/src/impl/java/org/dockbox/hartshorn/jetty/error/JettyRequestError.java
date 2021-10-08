package org.dockbox.hartshorn.jetty.error;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.DefaultCarrierContext;
import org.dockbox.hartshorn.web.error.RequestError;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
public class JettyRequestError extends DefaultCarrierContext implements RequestError {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final int statusCode;
    private final PrintWriter writer;
    private final Exceptional<Throwable> cause;
    @Setter private String message;
    @Setter private boolean yieldDefaults = false;

    public JettyRequestError(ApplicationContext applicationContext, HttpServletRequest request, HttpServletResponse response, int statusCode, PrintWriter writer, String message, Throwable cause) {
        super(applicationContext);
        this.request = request;
        this.response = response;
        this.statusCode = statusCode;
        this.writer = writer;
        this.message = message;
        this.cause = Exceptional.of(cause, cause);
    }
}
