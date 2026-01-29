package org.dockbox.hartshorn.web.report;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.web.filter.RequestFilter;

public class RequestFilterReporter implements Reportable {

    private final RequestFilter requestFilter;

    public RequestFilterReporter(RequestFilter requestFilter) {
        this.requestFilter = requestFilter;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("type").writeString(this.requestFilter.getClass().getName());
        collector.property("order").writeInt(this.requestFilter.order());
        if (this.requestFilter instanceof Reportable reportable) {
            collector.property("details").writeDelegate(reportable);
        }
    }
}
