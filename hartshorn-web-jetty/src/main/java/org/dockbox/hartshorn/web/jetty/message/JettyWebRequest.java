package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.HttpMessageHeaders;
import org.dockbox.hartshorn.web.message.HttpMessageQuery;
import org.dockbox.hartshorn.web.message.HttpRequestBody;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebRequestClient;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.Fields;

public class JettyWebRequest implements WebRequest {

    private final Request request;

    public JettyWebRequest(Request request) {
        this.request = request;
    }

    public Request underlyingRequest() {
        return this.request;
    }

    @Override
    public HttpMethod method() {
        String method = request.getMethod();
        return HttpMethod.fromString(method).orElse(null);
    }

    @Override
    public HttpMessageHeaders headers() {
        return new JettyHttpMessageHeaders(this.request.getHeaders());
    }

    @Override
    public HttpMessageQuery query() {
        Fields queryParameters = Request.extractQueryParameters(request);
        return new JettyHttpMessageQuery(queryParameters);
    }

    @Override
    public HttpRequestBody body() {
        return new JettyHttpRequestBody(this.request);
    }

    @Override
    public WebRequestClient client() {
        return new JettyWebRequestClient(this.request);
    }

    @Override
    public String path() {
        return Request.getPathInContext(request);
    }
}
