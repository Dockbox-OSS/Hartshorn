package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.web.HttpMethod;

public interface WebRequest {

    HttpMethod method();

    HttpMessageHeaders headers();

    HttpMessageQuery query();

    String path();
}
