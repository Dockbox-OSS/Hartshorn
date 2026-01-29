/*
 * Copyright 2019-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.web.HttpMethod;

import java.util.Map;
import org.dockbox.hartshorn.web.filter.PathMatchingRequestFilter;

/**
 * A {@link WebRequest} implementation that is aware of path parameters. Should only be created by
 * {@link PathMatchingRequestFilter} when a matching route is found.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class PathParamAwareWebRequest implements WebRequest {

    private final WebRequest delegate;
    private final MapWebRequestPathParameters pathParameters;

    public PathParamAwareWebRequest(WebRequest delegate, Map<String, String> pathParameters) {
        this.delegate = delegate;
        this.pathParameters = new MapWebRequestPathParameters(pathParameters);
    }

    @Override
    public String path() {
        return this.delegate.path();
    }

    @Override
    public HttpMethod method() {
        return this.delegate.method();
    }

    @Override
    public HttpMessageHeaders headers() {
        return this.delegate.headers();
    }

    @Override
    public HttpMessageQuery query() {
        return this.delegate.query();
    }

    @Override
    public HttpMessageBody body() {
        return this.delegate.body();
    }

    @Override
    public WebRequestClient client() {
        return this.delegate.client();
    }

    @Override
    public WebRequestPathParameters pathParameters() {
        return this.pathParameters;
    }
}
