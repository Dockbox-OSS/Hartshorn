package org.dockbox.hartshorn.web.chain;

import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UncapturedRequestFilter implements RequestFilter {

    @Override
    public void handle(
        WebRequest request,
        WebResponse response,
        RequestFilterChain chain
    ) throws Exception {
        // TODO: Configurable 404 handling
        response.status(404);
        response.headers().set("Content-Type", "text/html; charset=UTF-8");
        ByteBuffer buffer = ByteBuffer.wrap("""
            <h1>404 Not Found</h1>
            <p>The requested resource was not found on this server.</p>
            """.getBytes(StandardCharsets.UTF_8));
        response.write(buffer);
    }

    @Override
    public int order() {
        return ProcessingPriority.LOWEST_PRECEDENCE;
    }
}
