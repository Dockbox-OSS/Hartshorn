package org.dockbox.hartshorn.web.chain;

import java.util.List;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

public class SimpleRequestHandlerChain implements RequestHandlerChain {

    private final List<RequestHandlerStrategy> strategies;
    private final int index;

    public SimpleRequestHandlerChain(List<RequestHandlerStrategy> strategies) {
        this(strategies, 0);
    }

    public SimpleRequestHandlerChain(List<RequestHandlerStrategy> strategies, int index) {
        // TODO: Document that strategies must be sorted by order prior to passing them in
        //      This is to avoid sorting them on every step of every request.
        this.strategies = strategies;
        this.index = index;
    }

    @Override
    public void accept(WebRequest request, WebResponse response) throws Exception {
        if (this.index < this.strategies.size()) {
            RequestHandlerStrategy currentStrategy = this.strategies.get(this.index);
            RequestHandlerChain nextChain = new SimpleRequestHandlerChain(
                this.strategies,
                this.index + 1
            );
            currentStrategy.handle(request, response, nextChain);
        }
    }
}
