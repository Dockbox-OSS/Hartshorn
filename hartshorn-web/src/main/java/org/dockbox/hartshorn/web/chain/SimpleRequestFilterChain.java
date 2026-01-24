package org.dockbox.hartshorn.web.chain;

import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

import java.util.List;

public class SimpleRequestFilterChain implements RequestFilterChain {

    private final List<RequestFilter> filters;
    private final int index;

    public SimpleRequestFilterChain(List<RequestFilter> filters) {
        this(filters, 0);
    }

    public SimpleRequestFilterChain(List<RequestFilter> filters, int index) {
        // TODO: Document that strategies must be sorted by order prior to passing them in
        //      This is to avoid sorting them on every step of every request.
        this.filters = filters;
        this.index = index;
    }

    @Override
    public void accept(WebRequest request, WebResponse response) throws Exception {
        if (this.index < this.filters.size()) {
            RequestFilter currentFilter = this.filters.get(this.index);
            RequestFilterChain nextChain = new SimpleRequestFilterChain(
                this.filters,
                this.index + 1
            );
            currentFilter.handle(request, response, nextChain);
        }
    }
}
