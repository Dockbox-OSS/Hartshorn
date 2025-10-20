package org.dockbox.sample.reporting;

import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;

@Configuration
public class CustomReportingConfiguration {

    @Singleton
    @CompositeMember
    public CategorizedDiagnosticsReporter customReporter() {
        return new CustomDiagnosticsReporter();
    }
}
