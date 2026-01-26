package org.dockbox.hartshorn.web.message;

import java.io.IOException;

public interface HttpMessageBody {

    byte[] bytes() throws IOException;

    String asString() throws IOException;
}
