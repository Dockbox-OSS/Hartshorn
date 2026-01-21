package org.dockbox.hartshorn.web.chain;

import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

public interface RequestHandlerStrategy {

    void handle(
        WebRequest request,
        WebResponse response,
        RequestHandlerChain chain
    ) throws Exception;

    /**
     * Returns the order of the strategy. The order value is used to determine the order in which
     * the various strategies are executed. The lower the priority, the earlier the strategy will
     * be executed.
     *
     * @return The phase of when the strategy should be executed.
     */
    int order();
}
