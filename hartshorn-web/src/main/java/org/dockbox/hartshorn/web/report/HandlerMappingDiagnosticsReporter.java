package org.dockbox.hartshorn.web.report;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.web.route.PathHandlerSpec;
import org.dockbox.hartshorn.web.spec.PathSpec;

import java.util.Collection;

public class HandlerMappingDiagnosticsReporter implements Reportable {

    private final PathSpec pathSpec;
    private final Reportable handlerSpecsReporter;

    public HandlerMappingDiagnosticsReporter(
            PathSpec pathSpec,
            Collection<PathHandlerSpec> pathHandlerSpecs
    ) {
        this.pathSpec = pathSpec;
        this.handlerSpecsReporter = new PathHandlerSpecsDiagnosticsReporter(pathHandlerSpecs);
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("path").writeString(this.pathSpec.pattern());
        collector.property("handlers").writeDelegate(this.handlerSpecsReporter);
    }

    private record PathHandlerSpecsDiagnosticsReporter(
            Collection<PathHandlerSpec> pathHandlerSpecs
    ) implements Reportable {

        @Override
        public void report(DiagnosticsPropertyCollector delegate) {
            for (PathHandlerSpec spec : this.pathHandlerSpecs) {
                DiagnosticsPropertyWriter writer = delegate.property(spec.method().name());
                if (spec.handler() instanceof Reportable reportable) {
                    writer.writeDelegate(reportable);
                } else {
                    writer.writeString(spec.handler().toString());
                }
            }
        }
    }
}
