package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.web.HttpMethod;

import java.util.Map;

public class PathParamAwareWebRequest implements WebRequest {

    private final WebRequest delegate;
    private final MapWebRequestPathParameters pathParameters;

    public PathParamAwareWebRequest(WebRequest delegate, Map<String, String> pathParameters) {
        this.delegate = delegate;
        this.pathParameters = new MapWebRequestPathParameters(pathParameters);
    }

    @Override
    public String path() {
        return delegate.path();
    }

    @Override
    public HttpMethod method() {
        return delegate.method();
    }

    @Override
    public HttpMessageHeaders headers() {
        return delegate.headers();
    }

    @Override
    public HttpMessageQuery query() {
        return delegate.query();
    }

    @Override
    public HttpMessageBody body() {
        return delegate.body();
    }

    @Override
    public WebRequestClient client() {
        return delegate.client();
    }

    @Override
    public WebRequestPathParameters pathParameters() {
        return this.pathParameters;
    }
}
