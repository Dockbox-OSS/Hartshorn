package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.StringUtilities;

public class ProviderContextReportable implements Reportable {
    private final ProviderContext context;

    public ProviderContextReportable(final ProviderContext context) {
        this.context = context;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector elementCollector) {
        elementCollector.property("key").write(keyCollector -> {
            keyCollector.property("type").write(this.context.key().type().getCanonicalName());
            if (this.context.key().name() != null) {
                keyCollector.property("name").write(this.context.key().name());
            }
        });
        elementCollector.property("phase").write(this.context.provider().phase());
        elementCollector.property("lazy").write(this.context.provider().lazy());
        elementCollector.property("priority").write(this.context.provider().priority());
        if (StringUtilities.notEmpty(this.context.provider().value())) {
            elementCollector.property("name").write(this.context.provider().value());
        }
        elementCollector.property("element").write(this.context.element());
    }
}
