package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.message.HttpMessageBody;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Request;

import java.io.IOException;

public class JettyHttpMessageBody implements HttpMessageBody {

    private final Request request;

    public JettyHttpMessageBody(Request request) {
        this.request = request;
    }

    @Override
    public byte[] bytes() throws IOException {
        return Content.Source.asByteBuffer(request).array();
    }

    @Override
    public String asString() throws IOException {
        return Content.Source.asString(request);
    }
}
