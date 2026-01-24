package org.dockbox.hartshorn.web.chain;

import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

public interface RequestFilterChain {

    void accept(WebRequest request, WebResponse response) throws Exception;
}
