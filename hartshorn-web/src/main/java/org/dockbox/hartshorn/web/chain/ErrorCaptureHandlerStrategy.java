package org.dockbox.hartshorn.web.chain;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

public class ErrorCaptureHandlerStrategy implements RequestHandlerStrategy {

    private final ExceptionHandler exceptionHandler;

    public ErrorCaptureHandlerStrategy(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void handle(
        WebRequest request,
        WebResponse response,
        RequestHandlerChain chain
    ) throws Exception {
        try {
            chain.accept(request, response);
        }
        catch (Throwable throwable) {
            this.exceptionHandler.handle(
                "An error occurred while processing a web request",
                throwable
            );
            this.handleError(throwable, response);
        }
    }

    @Override
    public int order() {
        return ProcessingPriority.HIGHEST_PRECEDENCE;
    }

    private void handleError(Throwable throwable, WebResponse response) throws Exception {
        response.status(500);
        response.headers().set("Content-Type", "text/html; charset=UTF-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        throwable.printStackTrace(writer);
        writer.flush();

        ByteBuffer buffer = ByteBuffer.wrap("""
            <html>
                <head><title>Internal Server Error</title></head>
                <body>
                    <h1>500 - Internal Server Error</h1>
                    <p>An unexpected error occurred while processing your request.</p>
                    <pre>%s</pre>
                </body>
            </html>
            """
            .formatted(out.toString(StandardCharsets.UTF_8))
            .getBytes(StandardCharsets.UTF_8)
        );
        response.write(buffer);
    }
}
