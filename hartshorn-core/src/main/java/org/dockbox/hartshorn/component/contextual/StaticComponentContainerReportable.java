package org.dockbox.hartshorn.component.contextual;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;

public class StaticComponentContainerReportable implements Reportable {
    private final StaticComponentContainer<?> componentReference;

    public StaticComponentContainerReportable(final StaticComponentContainer<?> componentReference) {
        this.componentReference = componentReference;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector containerCollector) {
        containerCollector.property("type").write(this.componentReference.type());
        containerCollector.property("id").write(this.componentReference.id());
        containerCollector.property("instance").write(this.componentReference.instance().toString());
    }
}
