package org.dockbox.sample.reporting;

import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.ApplicationStarter;
import org.dockbox.hartshorn.launchpad.environment.FileSystemProvider;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.ReportSerializationException;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.serialize.ObjectMapperReportSerializer;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ApplicationReportingStarter implements ApplicationStarter {

    private final Reportable reportable;
    private final DiagnosticsReportCollector reportCollector;
    private final FileSystemProvider fileSystemProvider;
    private final Logger logger;

    public ApplicationReportingStarter(
        Reportable reportable,
        DiagnosticsReportCollector reportCollector,
        FileSystemProvider fileSystemProvider,
        Logger logger
    ) {
        this.reportable = reportable;
        this.reportCollector = reportCollector;
        this.fileSystemProvider = fileSystemProvider;
        this.logger = logger;
    }

    @Override
    public void run(ApplicationContext applicationContext) {
        DiagnosticsReport diagnosticsReport = this.reportCollector.report(this.reportable);
        try {
            String serializedReport =
                diagnosticsReport.serialize(new ObjectMapperReportSerializer.JsonReportSerializer());
            Path targetFile = this.fileSystemProvider.applicationPath()
                .resolve("hartshorn-launchpad-samples")
                .resolve("application-reporting")
                .resolve("reports")
                .resolve("diagnostics-report-%s.json".formatted(System.currentTimeMillis()));
            Files.createDirectories(targetFile.getParent());
            Files.writeString(targetFile, serializedReport);
            this.logger.info("Diagnostics report written to {}", targetFile);
        }
        catch (IOException | ReportSerializationException e) {
            throw new RuntimeException(e);
        }
    }
}
