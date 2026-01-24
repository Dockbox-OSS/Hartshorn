package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.web.HttpMethod;

public interface WebRequest {

    String path();

    HttpMethod method();

    HttpMessageHeaders headers();

    HttpMessageQuery query();

    HttpRequestBody body();
}
