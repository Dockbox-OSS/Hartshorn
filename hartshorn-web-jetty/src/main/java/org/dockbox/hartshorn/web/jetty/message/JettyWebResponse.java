package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.message.MutableHttpMessageHeaders;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;

import java.nio.ByteBuffer;

public class JettyWebResponse implements WebResponse {

    private final Response response;

    public JettyWebResponse(Response response) {
        this.response = response;
    }

    @Override
    public void write(ByteBuffer data) throws Exception {
        Content.Sink.write(this.response, true, data);
    }

    @Override
    public void status(int status) {
        this.response.setStatus(status);
    }

    @Override
    public MutableHttpMessageHeaders headers() {
        return new JettyHttpMessageHeaders(this.response.getHeaders());
    }

    @Override
    public boolean committed() {
        return this.response.isCommitted();
    }
}
