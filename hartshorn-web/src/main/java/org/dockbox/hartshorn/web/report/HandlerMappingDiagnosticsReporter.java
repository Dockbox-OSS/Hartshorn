/*
 * Copyright 2019-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
