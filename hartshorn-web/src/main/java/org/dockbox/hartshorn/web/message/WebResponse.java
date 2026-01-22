package org.dockbox.hartshorn.web.message;

import java.nio.ByteBuffer;

public interface WebResponse {

    void write(ByteBuffer data) throws Exception;

    // TODO: Use pre-defined status codes
    void status(int status);

    MutableHttpMessageHeaders headers();

    boolean committed();
}
