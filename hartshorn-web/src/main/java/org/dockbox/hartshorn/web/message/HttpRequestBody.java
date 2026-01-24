package org.dockbox.hartshorn.web.message;

import java.io.IOException;

public interface HttpRequestBody {

    byte[] bytes() throws IOException;

    String asString() throws IOException;
}
